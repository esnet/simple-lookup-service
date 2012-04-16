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
import net.es.mp.measurement.types.MeasurementResult;
import net.es.mp.measurement.types.OWAMPMeasurement;
import net.es.mp.measurement.types.OWAMPResult;
import net.es.mp.scheduler.MPSchedulingService;
import net.es.mp.scheduler.types.OWAMPSchedule;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.types.parameters.OWAMPParams;
import net.es.mp.util.archivers.Archiver;
import net.es.mp.util.archivers.LocalArchiver;
import net.es.mp.util.publishers.LocalStreamPublisher;
import net.es.mp.util.publishers.Publisher;

import org.apache.log4j.Logger;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.mongodb.BasicDBObject;

public class OWAMPJob  extends ExecCommandJob{
    private Logger log = Logger.getLogger(OWAMPJob.class);
    private Logger netlogger = Logger.getLogger("netlogger");
    private long timeout;
    private String controller;
    private String commandPath;

    final private static String PROP_CONTROLLER_HOST = "controllerHost";
    final private static String PROP_COMMAND = "command";
    final private static String PROP_TIMEOUT = "timeout";
    final private static String DEFAULT_COMMAND = "owping";
    final private static String DEFAULT_ERROR_MSG = "The owping command returned an unknown error";
    final private static String[] ALLOWED_HOST_PREFS = {OWAMPParams.SOURCE, OWAMPParams.DESTINATION, 
                                                            OWAMPParams.CONTROLLER};
    
    public void init(Schedule schedule, AuthzConditions authzConditions){
        //read config
        MPSchedulingService globals = MPSchedulingService.getInstance();
        Map config = MPSchedulingService.getInstance().getToolConfig(OWAMPParams.TYPE_VALUE);
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
            Integer count = ((OWAMPSchedule) schedule).getPacketCount();
            if(count == null){
                count = OWAMPParams.DEFAULT_PACKET_COUNT;
            }
            Double wait = ((OWAMPSchedule) schedule).getPacketWait();
            if(wait == null){
                wait = OWAMPParams.DEFAULT_PACKET_WAIT;
            }
            this.timeout = 3*((int)(count*wait)) + 30L ;
        }
        
        //configure ssh
        if(config.containsKey(SSHConfig.PROP_SSH)){
            this.sshConfig = SSHConfig.fromYAML((Map)config.get(SSHConfig.PROP_SSH), ALLOWED_HOST_PREFS, 
                    this.controller);
            this.sshConfig.setScheduleHostField(OWAMPParams.CONTROLLER);
        }
        
        //user provided command arguments
        this.commandArgs.add(((OWAMPSchedule) schedule).getDestination());

        //user provided options
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.SOURCE, "-S"));
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.PACKET_COUNT, "-c", 
                OWAMPParams.DEFAULT_PACKET_COUNT+""));
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.PACKET_WAIT, "-i", 
                OWAMPParams.DEFAULT_PACKET_WAIT+""));
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.PACKET_TIMEOUT, "-L"));
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.PACKET_PADDING, "-s"));
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.DSCP, "-D"));
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.TEST_PORTS, "-P"));

        //user provided flags
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.IP_VERSION, "-4", true, 
                OWAMPParams.IPV_V4_ONLY));
        this.userCommandOpts.add(new ExecCommandOpt(OWAMPParams.IP_VERSION, "-6", true, 
                OWAMPParams.IPV_V6_ONLY));

        //TODO: server set options and flags
        //make sure it only returns one-way
        this.providerCommandOpts.add("-t");
        //machine readbale output
        //this.providerCommandOpts.add("-M");
        
        //populate known errors
        this.knownErrors.add(new ExecCommandError(
                Pattern.compile("owping:.*,\\s+(Unable to open control connection to .*)"), 
                OWAMPResult.STATUS_CTRL_CONNECT_FAILED));
        this.knownErrors.add(new ExecCommandError(
                Pattern.compile("owping:.*,\\s+getaddrinfo.*"), 
                OWAMPResult.STATUS_CTRL_CONNECT_FAILED,
        "Invalid hostname provided"));
        this.knownErrors.add(new ExecCommandError(
                Pattern.compile("owping:.*,\\s+(Server denied test.*)"), 
                OWAMPResult.STATUS_DENIED));
    }

    protected long getTimeout() {
        return this.timeout;
    }

    protected String getToolName() {
        return this.commandPath;
    }

    protected void handleOutput(BufferedReader stdout, BufferedReader stderr,
            Schedule schedule) throws IOException {
        OWAMPSchedule owampSchedule = (OWAMPSchedule) schedule;

        //Init measurement
        OWAMPMeasurement measurement = this.buildMeasurement(owampSchedule);

        //Build result
        MeasurementResult result = new OWAMPResult(new BasicDBObject());
        result.setStatus(MeasurementResult.STATUS_OK);
        Pattern ipLinePattern = Pattern.compile("---\\s*owping\\s+statistics\\s+from\\s+\\[(.+)\\]:\\d+\\s+to\\s+\\[(.+)\\]:\\d+\\s*---");
        Pattern startPattern = Pattern.compile("first:\\s+(.+)");
        Pattern endPattern = Pattern.compile("last:\\s+(.+)");
        Pattern lossPattern = Pattern.compile("\\d+\\s+sent,\\s+(\\d+)\\s+lost\\s+\\(\\d+(\\.\\d+)?%\\),\\s+(\\d+)\\s+duplicates");
        Pattern delayPattern = Pattern.compile("one-way\\s+delay\\s+min/median/max\\s+=\\s+(.+)/(.+)/(.+)\\s+ms,\\s+\\((err=)?(-?[0-9\\.]+)?.*?\\)");
        Pattern ttlPattern1 = Pattern.compile("Hops\\s+=\\s+(\\d+)\\s+\\(consistently\\)");
        Pattern ttlPattern2 = Pattern.compile("TTL\\s+not\\s+reported");
        Pattern ttlPattern3 = Pattern.compile("Hops\\s+takes\\s+(\\d+)\\s+values;\\s+Min\\s+Hops\\s+=\\s+(\\d+),\\s+Max\\s+Hops\\s+=\\s+(\\d+)");
        DateTimeFormatter dateParser = ISODateTimeFormat.localDateOptionalTimeParser().withZone(DateTimeZone.getDefault());

        String outputLine = null;
        boolean ipsFound = false;
        boolean startFound = false;
        boolean endFound = false;
        boolean lossFound = false;
        boolean delayFound = false;
        boolean ttlFound = false;
        String returnedSource = "";
        String returnedDest = "";
        while((outputLine = stdout.readLine()) != null){
            log.debug(outputLine.trim());
            Matcher ipLineMatcher = ipLinePattern.matcher(outputLine.trim());
            Matcher startMatcher = startPattern.matcher(outputLine.trim());
            Matcher endMatcher = endPattern.matcher(outputLine.trim());
            Matcher lossMatcher = lossPattern.matcher(outputLine.trim());
            Matcher delayMatcher = delayPattern.matcher(outputLine.trim());
            Matcher ttlMatcher1 = ttlPattern1.matcher(outputLine.trim());
            Matcher ttlMatcher2 = ttlPattern2.matcher(outputLine.trim());
            Matcher ttlMatcher3 = ttlPattern3.matcher(outputLine.trim());

            if(ipLineMatcher.matches()){
                returnedSource = ipLineMatcher.group(1);
                returnedDest = ipLineMatcher.group(2);
                ipsFound = true;
            }else if(startMatcher.matches()){
                result.setStartTime(dateParser.parseDateTime(startMatcher.group(1)).toDate());
                //result.setStartTime(new Date());
                startFound = true;
            }else if(endMatcher.matches()){
                result.setEndTime(dateParser.parseDateTime(endMatcher.group(1)).toDate());
                //result.setEndTime(new Date());
                endFound = true;
            }else if(lossMatcher.matches()){
                ((OWAMPResult)result).setLoss(Integer.parseInt(lossMatcher.group(1)));
                ((OWAMPResult)result).setDuplicates(Integer.parseInt(lossMatcher.group(3)));
                lossFound = true;
            }else if(delayMatcher.matches()){
                try{
                    ((OWAMPResult)result).setMinDelay(Double.parseDouble(delayMatcher.group(1)));
                    ((OWAMPResult)result).setMedianDelay(Double.parseDouble(delayMatcher.group(2)));
                    ((OWAMPResult)result).setMaxDelay(Double.parseDouble(delayMatcher.group(3)));
                    if(delayMatcher.groupCount() >= 5 && delayMatcher.group(5) != null){
                        ((OWAMPResult)result).setMaxError(Double.parseDouble(delayMatcher.group(5)));
                    }else{
                        ((OWAMPResult)result).setMaxError(null);
                    }
                    delayFound = true;
                }catch(Exception e){
                    //handle NaN issues, treat this like an error condition for now
                    continue;
                }
            }else if(ttlMatcher1.matches()){
                ((OWAMPResult)result).setMinTTL(Integer.parseInt(ttlMatcher1.group(1)));
                ((OWAMPResult)result).setMaxTTL(Integer.parseInt(ttlMatcher1.group(1)));
                ttlFound = true;
            }else if(ttlMatcher2.matches()){
                //same host
                ((OWAMPResult)result).setMinTTL(0);
                ((OWAMPResult)result).setMaxTTL(0);
                ttlFound = true;
            }else if(ttlMatcher3.matches()){
                ((OWAMPResult)result).setMinTTL(Integer.parseInt(ttlMatcher3.group(2)));
                ((OWAMPResult)result).setMaxTTL(Integer.parseInt(ttlMatcher3.group(3)));
                ttlFound = true;
            }
        }
        
        System.out.println(ipsFound + " " + startFound + " " + endFound + " " +
                lossFound + " " + delayFound + " " + ttlFound);
        //check for errors
        log.debug("STDERROR:");
        String[] errArr = this.parseErrorMsg(stderr, DEFAULT_ERROR_MSG);
        if(errArr != null){
            result = this.buildErrorResult(errArr[0], errArr[1], result.getStartTime(), result.getEndTime());
        }else if(!ipsFound){
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, 
                    "Unable to parse IP addresses in owping output", 
                    result.getStartTime(), 
                    result.getEndTime());
        }else if(!startFound){
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, 
                    "Unable to parse start time in owping output", 
                    result.getStartTime(), 
                    result.getEndTime());
        }else if(!endFound){
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, 
                    "Unable to parse end time in owping output", 
                    result.getStartTime(), 
                    result.getEndTime());
        }else if(!lossFound){
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, 
                    "Unable to parse loss in owping output", 
                    result.getStartTime(), 
                    result.getEndTime());
        }else if(!delayFound){
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, 
                    "Unable to parse delay in owping output", 
                    result.getStartTime(), 
                    result.getEndTime());
        }else if(!ttlFound){
            result = this.buildErrorResult(MeasurementResult.STATUS_ERROR, 
                    "Unable to parse TTL values in owping output", 
                    result.getStartTime(), 
                    result.getEndTime());
        }
        measurement.setResult(result);

        //figure out hostname of source and destination
        try{
            InetAddress inet = InetAddress.getByName(returnedSource);
            String hostname = inet.getCanonicalHostName();
            String ip = inet.getHostAddress();
            measurement.setSourceIP(ip);
            //if get back textual representation of IP then se to null
            if(!ip.equals(hostname)){
                measurement.setSourceHostname(hostname);
            }else if(!measurement.getSource().equals(hostname)){
                //this only happens if for some reason can't get hostname but source was specified as host
                measurement.setSourceHostname(measurement.getSource());
            }
        }catch(Exception e){
            //can't resolve so set to null
            log.debug("Unable to set sourceHostname: " + e.getMessage());
            measurement.setSourceHostname(null);
        }
        try{
            InetAddress inet = InetAddress.getByName(returnedDest);
            String hostname = inet.getCanonicalHostName();
            String ip = inet.getHostAddress();
            measurement.setDestinationIP(ip);
            //if get back textual representation of IP then se to null
            if(!ip.equals(hostname)){
                measurement.setDestinationHostname(hostname);
            }else if(!measurement.getDestination().equals(hostname)){
                //this only happens if for some reason can't get hostname but dest was specified as host
                measurement.setDestinationHostname(measurement.getDestination());
            }
        }catch(Exception e){
            //can't resolve so set to null
            log.debug("Unable to set destinationHostname: " + e.getMessage());
            measurement.setDestinationHostname(null);
        }

        //archive and publish measurement
        this.archiveAndPublish(measurement, schedule.getStreamURI());

        System.out.println(measurement.toJSONString());

    }

    protected void handleError(int resultCode, BufferedReader stdout,
            BufferedReader stderr, Schedule schedule) throws IOException {
        OWAMPSchedule owamSchedule = (OWAMPSchedule) schedule;
        
        //Init measurement
        OWAMPMeasurement measurement = this.buildMeasurement(owamSchedule);
        
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
        OWAMPSchedule owampSchedule = (OWAMPSchedule) schedule;
        OWAMPMeasurement measurement = this.buildMeasurement(owampSchedule);
        MeasurementResult result = this.buildErrorResult(
                MeasurementResult.STATUS_ERROR, 
                "A timeout occurred because the owping command did not return after " + this.timeout + " seconds.", 
                null, null);
        measurement.setResult(result);
        this.archiveAndPublish(measurement, schedule.getStreamURI());
    }

    private OWAMPMeasurement buildMeasurement(OWAMPSchedule owampSchedule){
        OWAMPMeasurement measurement = new OWAMPMeasurement(new BasicDBObject());

        //      required by measurement and schedule
        measurement.setType(owampSchedule.getType());
        measurement.setScheduleURI(owampSchedule.getURI());
        measurement.setSource(owampSchedule.getSource());
        measurement.setDestination(owampSchedule.getDestination());
        //    required by measurement but not schedule
        if(owampSchedule.getController() == null){
            measurement.setController(this.controller);
        }else{
            measurement.setController(owampSchedule.getController());
        }
        if(owampSchedule.getIPVersion() == null){
            measurement.setIPVersion(OWAMPParams.IPV_PREFER_V6);
        }else{
            measurement.setIPVersion(owampSchedule.getIPVersion());
        }
        if(owampSchedule.getPacketCount() == null){
            measurement.setPacketCount(OWAMPParams.DEFAULT_PACKET_COUNT);
        }else{
            measurement.setPacketCount(owampSchedule.getPacketCount());
        }
        if(owampSchedule.getPacketWait() == null){
            measurement.setPacketWait(OWAMPParams.DEFAULT_PACKET_WAIT);
        }else{
            measurement.setPacketWait(owampSchedule.getPacketWait());
        }

        //    optional parameters
        if(owampSchedule.getPacketTimeout() != null){
            measurement.setPacketTimeout(owampSchedule.getPacketTimeout());
        }
        if(owampSchedule.getPacketPadding() != null){
            measurement.setPacketPadding(owampSchedule.getPacketPadding());
        }
        if(owampSchedule.getDSCP() != null){
            measurement.setDSCP(owampSchedule.getDSCP());
        }
        if(owampSchedule.getTestPorts() != null){
            measurement.setTestPorts(owampSchedule.getTestPorts());
        }

        //init these fields
        measurement.setSourceHostname(null);
        measurement.setDestinationHostname(null);
        measurement.setSourceIP(null);
        measurement.setDestinationIP(null);

        return measurement;
    }

    private void archiveAndPublish(OWAMPMeasurement measurement, String streamUri) {
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
