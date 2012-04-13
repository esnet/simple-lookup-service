package net.es.mp.scheduler.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.es.mp.authz.AuthzConditions;
import net.es.mp.measurement.types.Measurement;
import net.es.mp.measurement.types.BWCTLMeasurement;
import net.es.mp.measurement.types.BWCTLResult;
import net.es.mp.measurement.types.BWCTLResultInterval;
import net.es.mp.measurement.types.MeasurementResult;
import net.es.mp.scheduler.MPSchedulingService;
import net.es.mp.scheduler.types.BWCTLSchedule;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.types.parameters.BWCTLParams;
import net.es.mp.util.TimeUtil;
import net.es.mp.util.archivers.Archiver;
import net.es.mp.util.archivers.LocalArchiver;
import net.es.mp.util.publishers.LocalStreamPublisher;
import net.es.mp.util.publishers.Publisher;

import org.apache.log4j.Logger;

import com.mongodb.BasicDBObject;


public class BWCTLJob extends ExecCommandJob{
    private Logger log = Logger.getLogger(BWCTLJob.class);
    private Logger netlogger = Logger.getLogger("netlogger");
    private long timeout;
    private String controller;
    private String commandPath;
    
    final private static String PROP_CONTROLLER_HOST = "controllerHost";
    final private static String PROP_COMMAND = "command";
    final private static String PROP_TIMEOUT = "timeout";
    final private static String DEFAULT_COMMAND = "bwctl";
    final private static String DEFAULT_ERROR_MSG = "The bwctl command returned an unknown error";
    
    public void init(Schedule schedule, AuthzConditions authzConditions){
        //read config
        MPSchedulingService globals = MPSchedulingService.getInstance();
        Map config = MPSchedulingService.getInstance().getToolConfig(BWCTLParams.TYPE_VALUE);
        if(config.containsKey(PROP_CONTROLLER_HOST) && 
                config.get(PROP_CONTROLLER_HOST) != null){
            this.controller = (String) config.get(PROP_CONTROLLER_HOST);
        }else{
            //default to this host
            this.controller = globals.getContainer().getWebServer().getHostname();
        }
        if(config.containsKey(PROP_COMMAND) && 
                config.get(PROP_COMMAND) != null){
            this.commandPath = (String) config.get(PROP_COMMAND);
        }else{
            //default to this host
            this.commandPath = DEFAULT_COMMAND;
        }
        if(config.containsKey(PROP_TIMEOUT) && 
                config.get(PROP_TIMEOUT) != null){
            this.timeout = (Integer) config.get(PROP_TIMEOUT);
        }else{
            //default based on duration
            this.timeout = ((BWCTLSchedule) schedule).getDuration() * 3L + 30L;
        }
        
        //user provided options
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.TOOL_TYPE, "-T"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.SOURCE, "-s"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.DESTINATION, "-c"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.DURATION, "-t"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.REPORT_INTERVAL, "-i"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.BUFFER_LENGTH, "-l"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.DSCP, "-D"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.TOS, "-S"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.PARALLEL_CONN, "-P"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.TCP_WINDOW_SIZE, "-w"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.TCP_DYN_WINDOW_SIZE, "-W"));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.UDP_BANDWIDTH, "-b"));
        
        //user provided flags
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.IP_VERSION, "-4", true, 
                BWCTLParams.IPV_V4_ONLY));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.IP_VERSION, "-6", true, 
                BWCTLParams.IPV_V6_ONLY));
        this.userCommandOpts.add(new ExecCommandOpt(BWCTLParams.PROTOCOL, "-u", true, 
                BWCTLParams.PROTOCOL_UDP));
        
        //TODO: server set options and flags
        
        //populate known errors
        this.knownErrors.add(new ExecCommandError(
                Pattern.compile("bwctl:\\s+(Unable to connect to .*)"), 
                BWCTLResult.STATUS_CTRL_CONNECT_FAILED));
        this.knownErrors.add(new ExecCommandError(
                Pattern.compile("bwctl:\\s+getaddrinfo.*"), 
                BWCTLResult.STATUS_CTRL_CONNECT_FAILED,
                "Invalid hostname provided"));
        this.knownErrors.add(new ExecCommandError(
                Pattern.compile("bwctl:\\s+(Server denied access.*)"), 
                BWCTLResult.STATUS_DENIED));
    }
    
    
    protected long getTimeout() {
        return this.timeout;
    }
    
    protected void handleOutput(BufferedReader stdout, BufferedReader stderr, Schedule schedule) throws IOException {
        BWCTLSchedule bwctlSchedule = (BWCTLSchedule) schedule;
        
        //Init measurement
        BWCTLMeasurement measurement = this.buildMeasurement(bwctlSchedule);
        
        //Build result
        MeasurementResult result = new BWCTLResult(new BasicDBObject());
        result.setStatus(MeasurementResult.STATUS_OK);
        Pattern intervalPattern = Pattern.compile("\\[\\s*\\d+\\s*\\]\\s*(\\d+)\\.\\d+\\s*\\-\\s*(\\d+)\\.\\d+\\s+sec\\s+(\\d+\\.?\\d*)\\s+(\\w+)\\s+(\\d+\\.?\\d*)\\s+(\\w+\\/\\w+).*?");
        Pattern ipLinePattern = Pattern.compile("\\[\\s*\\d+\\s*\\]\\s*local\\s+(.+?)\\s+port\\s+\\d+\\s+connected with\\s+(.+?)\\s+port\\s+\\d+.*?");
        Pattern startTimePattern = Pattern.compile("bwctl:\\s+start_tool:\\s+(\\d+)\\.(\\d+)");
        Pattern endTimePattern = Pattern.compile("bwctl:\\s+stop_exec:\\s+(\\d+)\\.(\\d+)");
        String outputLine = null;
        boolean throughputFound = false;
        boolean ipsFound = false;
        boolean startTimeFound = false;
        boolean endTimeFound = false;
        while((outputLine = stdout.readLine()) != null){
            log.debug(outputLine.trim());
            Matcher intervalMatcher = intervalPattern.matcher(outputLine.trim());
            Matcher ipLineMatcher = ipLinePattern.matcher(outputLine.trim());
            Matcher startTimeMatcher = startTimePattern.matcher(outputLine.trim());
            Matcher endTimeMatcher = endTimePattern.matcher(outputLine.trim());
            if(intervalMatcher.matches()){
                ((BWCTLResult)result).setThroughput(Long.parseLong("0.00".equals(intervalMatcher.group(5)) ? "0" : intervalMatcher.group(5)));
                if(bwctlSchedule.getReportInterval() != null){
                    BWCTLResultInterval tmpInterval = new BWCTLResultInterval(new BasicDBObject());
                    tmpInterval.setStart(Long.parseLong(intervalMatcher.group(1)));
                    tmpInterval.setEnd(Long.parseLong(intervalMatcher.group(2)));
                    tmpInterval.setThroughput(Long.parseLong(intervalMatcher.group(5)));
                    ((BWCTLResult)result).addInterval(tmpInterval);
                }
                throughputFound = true;
            }else if(ipLineMatcher.matches()){
                measurement.setDestinationIP(ipLineMatcher.group(1));
                measurement.setSourceIP(ipLineMatcher.group(2));
                ipsFound = true;
            }else if(startTimeMatcher.matches()){
                result.setStartTime(TimeUtil.owpTimeToDate(startTimeMatcher.group(1), 
                        startTimeMatcher.group(2)));
                startTimeFound = true;
            }else if(endTimeMatcher.matches()){
                result.setEndTime(TimeUtil.owpTimeToDate(endTimeMatcher.group(1), 
                        endTimeMatcher.group(2)));
                endTimeFound = true;
            }
        }
        
        //check for errors
        log.debug("STDERROR:");
        String[] errArr = this.parseErrorMsg(stderr, DEFAULT_ERROR_MSG);
        if(errArr != null){
            result = this.buildErrorResult(errArr[0], errArr[1], result.getStartTime(), result.getEndTime());
        }else if(!(throughputFound && ipsFound && startTimeFound && endTimeFound)){
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, 
                    DEFAULT_ERROR_MSG, 
                    result.getStartTime(), 
                    result.getEndTime());
        }
        measurement.setResult(result);
        
        //figure out hostname of source and destination
        try{
            String hostname = InetAddress.getByName(bwctlSchedule.getSource()).getCanonicalHostName();
            //if get back textual representation of IP then se to null
            if(measurement.getSourceIP() != null && measurement.getSourceIP().equals(hostname)){
                hostname = null;
            }
            measurement.setSourceHostname(hostname);
        }catch(Exception e){
            //can't resolve so set to null
            log.debug("Unable to set sourceHostname: " + e.getMessage());
            measurement.setSourceHostname(null);
            
        }
        try{
            String hostname = InetAddress.getByName(bwctlSchedule.getDestination()).getCanonicalHostName();
            if(measurement.getDestinationIP() != null && measurement.getDestinationIP().equals(hostname)){
                hostname = null;
            }
            measurement.setDestinationHostname(hostname);
        }catch(Exception e){
            //can't resolve so set to null
            log.debug("Unable to set destinationHostname: " + e.getMessage());
            measurement.setDestinationHostname(null);
        }
        
        //archive and publish measurement
        this.archiveAndPublish(measurement, schedule.getStreamURI());
        
        System.out.println(measurement.toJSONString());
    }

    protected String getToolName() {
        return this.commandPath;
    }


    protected void handleError(int resultCode, BufferedReader stdout, 
            BufferedReader stderr, Schedule schedule) throws IOException {
        BWCTLSchedule bwctlSchedule = (BWCTLSchedule) schedule;
        
        //Init measurement
        BWCTLMeasurement measurement = this.buildMeasurement(bwctlSchedule);
        
        //build result
        MeasurementResult result = null;
        String[] errArr = this.parseErrorMsg(stderr, DEFAULT_ERROR_MSG);
        if(errArr != null){
            result = this.buildErrorResult(errArr[0], errArr[1], null, null);
        }else{
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, DEFAULT_ERROR_MSG, null, null);
        }
        measurement.setResult(result);
        
        //archive and publish
        this.archiveAndPublish(measurement, schedule.getStreamURI());
    }
    
    protected void handleTimeout(Schedule schedule) {
        BWCTLSchedule bwctlSchedule = (BWCTLSchedule) schedule;
        BWCTLMeasurement measurement = this.buildMeasurement(bwctlSchedule);
        MeasurementResult result = this.buildErrorResult(
                MeasurementResult.STATUS_ERROR, 
                "A timeout occurred because the bwctl command did not return after " + this.timeout + " seconds.", 
                null, null);
        measurement.setResult(result);
        this.archiveAndPublish(measurement, schedule.getStreamURI());
    }
    
    private BWCTLMeasurement buildMeasurement(BWCTLSchedule bwctlSchedule){
        BWCTLMeasurement measurement = new BWCTLMeasurement(new BasicDBObject());
        
        //      required by measurement and schedule
        measurement.setType(bwctlSchedule.getType());
        measurement.setScheduleURI(bwctlSchedule.getURI());
        measurement.setToolType(bwctlSchedule.getToolType());
        measurement.setSource(bwctlSchedule.getSource());
        measurement.setDestination(bwctlSchedule.getDestination());
        measurement.setDuration(bwctlSchedule.getDuration());
        //    required by measurement but not schedule
        if(bwctlSchedule.getController() == null){
            measurement.setController(this.controller);
        }else{
            measurement.setController(bwctlSchedule.getController());
        }
        if(bwctlSchedule.getIPVersion() == null){
            measurement.setIPVersion(BWCTLParams.IPV_PREFER_V6);
        }else{
            measurement.setIPVersion(bwctlSchedule.getIPVersion());
        }
        if(bwctlSchedule.getProtocol() == null){
            measurement.setProtocol(BWCTLParams.PROTOCOL_TCP);
        }else{
            measurement.setProtocol(bwctlSchedule.getProtocol());
        }
        //    optional parameters
        if(bwctlSchedule.getReportInterval() != null){
            measurement.setReportInterval(bwctlSchedule.getReportInterval());
        }
        if(bwctlSchedule.getBufferLength() != null){
            measurement.setBufferLength(bwctlSchedule.getBufferLength());
        }
        if(bwctlSchedule.getDSCP() != null){
            measurement.setDSCP(bwctlSchedule.getDSCP());
        }
        if(bwctlSchedule.getTOS() != null){
            measurement.setTOS(bwctlSchedule.getTOS());
        }
        if(bwctlSchedule.getParallelConnections() != null){
            measurement.setParallelConnections(bwctlSchedule.getParallelConnections());
        }
        if(bwctlSchedule.getTCPWindowSize() != null){
            measurement.setTCPWindowSize(bwctlSchedule.getTCPWindowSize());
        }
        if(bwctlSchedule.getTCPDynamicWindowSize() != null){
            measurement.setTCPDynamicWindowSize(bwctlSchedule.getTCPDynamicWindowSize());
        }
        if(bwctlSchedule.getUDPBandwidth() != null){
            measurement.setUDPBandwidth(bwctlSchedule.getUDPBandwidth());
        }
        //init these fields
        measurement.setSourceHostname(null);
        measurement.setDestinationHostname(null);
        measurement.setSourceIP(null);
        measurement.setDestinationIP(null);
        
        return measurement;
    }
    
    private void archiveAndPublish(BWCTLMeasurement measurement, String streamUri) {
        //archive the measurement
          Archiver archiver = new LocalArchiver();
          archiver.archive(measurement);
          
          //publish to stream
          Publisher localPublisher = new LocalStreamPublisher();
          List<Measurement> measList = (new ArrayList<Measurement>());
          measList.add(measurement);
          localPublisher.publish(measList, streamUri);
      }
 
}
