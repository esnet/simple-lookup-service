package net.es.mp.scheduler.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;

import net.es.mp.authz.AuthzConditions;
import net.es.mp.measurement.types.MeasurementResult;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.scheduler.types.Schedule;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.mongodb.BasicDBObject;

public abstract class ExecCommandJob implements Job{
    private Logger log = Logger.getLogger(ExecCommandJob.class);
    private Logger netlogger = Logger.getLogger("netlogger");
    final private String EVENT = "mp.scheduler.ExecCommandJob.execute";
    protected List<ExecCommandOpt> userCommandOpts;
    protected List<String> providerCommandOpts;
    protected List<String> commandArgs;
    protected List<ExecCommandError> knownErrors;
    protected SSHConfig sshConfig;
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
        NetLogger netLog = NetLogger.getTlogger();
        Schedule schedule = (Schedule) context.getJobDetail().getJobDataMap().get("schedule");
        AuthzConditions authzConditions = (AuthzConditions) context.getJobDetail().getJobDataMap().get("authzConditions");
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        this.userCommandOpts = new ArrayList<ExecCommandOpt>();
        this.providerCommandOpts = new ArrayList<String>();
        this.commandArgs = new ArrayList<String>();
        this.knownErrors = new ArrayList<ExecCommandError>();
        this.sshConfig = new SSHConfig();
        this.init(schedule, authzConditions);
        
        //build the command
        String[] command = this.buildCommand(schedule);
        
        //run the command
        try {
            netlogger.debug(netLog.start(EVENT));
            log.debug("Executing command :");
            for(String opt : command){
                log.debug("    " + opt);
            }
            process = runtime.exec(command);
            WatchDog watchdog = new WatchDog(process);
            watchdog.start();
            watchdog.join(getTimeout()*1000);
            if(watchdog.exit == null){
                this.handleTimeout(schedule);
                this.log.debug("Command timed-out after " + getTimeout() + " seconds");
            }else{
                int resultCode = process.exitValue();
                BufferedReader stdin = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader stderr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                if(resultCode != 0){
                    this.handleError(resultCode, stdin, stderr, schedule);
                }else{
                    this.handleOutput(stdin, stderr, schedule);
                }
            }
            netlogger.debug(netLog.end(EVENT));
        } catch (Exception e) {
            netlogger.debug(netLog.error("EVENT", e.getMessage()));
            log.error("Error running command: " + e.getMessage());
            e.printStackTrace();
        } finally{
            if(process != null){
                process.destroy();
            }
        }

    }

    public void init(Schedule schedule, AuthzConditions authzConditions){}

    protected String[] buildCommand(Schedule schedule){
        List<String> commandList = new ArrayList<String>();
        
        //add ssh parameters if needed
        this.configureSSH(commandList, schedule);
        
        //add tool name
        commandList.add(this.getToolName());
        //add user options
        for(ExecCommandOpt cmdOpt : this.userCommandOpts){
            //check if key exists and handle setting value
            Object optValue = null;
            if(!schedule.getDBObject().containsField(cmdOpt.getKeyName()) ||
                    schedule.getDBObject().get(cmdOpt.getKeyName()) == null){
                if(cmdOpt.getDefaultValue() == null){
                    continue;
                }else{
                    optValue = cmdOpt.getDefaultValue();
                }
            }else{
                optValue = schedule.getDBObject().get(cmdOpt.getKeyName());
            }

            if(cmdOpt.isFlag() && (cmdOpt.getReqValueForFlag() == null ||
                    cmdOpt.getReqValueForFlag().equals(optValue))){
                //handle flag
                commandList.add(cmdOpt.getOptionName());
            }else if(!cmdOpt.isFlag() && cmdOpt.getOptionName() != null){
                //handle option
                commandList.add(cmdOpt.getOptionName());
                commandList.add(optValue.toString());
            }else if(!cmdOpt.isFlag()){
                //handle argument
                commandList.add(optValue.toString());
            }
        }
        
        //add provider opts
        for(String providerOpt: this.providerCommandOpts){
            commandList.add(providerOpt);
        }
        
        //add arguments
        commandList.addAll(this.commandArgs);
        
        //convert to array
        String[] command = new String[commandList.size()];
        commandList.toArray(command);

        return command;
    }
    
    protected String[] parseErrorMsg(BufferedReader stderr, String defaultErrorMsg) throws IOException {
        String[] errArr = null;
        String errLine = null;
        boolean errFound = false;
        while((errLine = stderr.readLine()) != null){
            log.debug(errLine);
            for(ExecCommandError knownError : this.knownErrors){
                Matcher errMatcher = knownError.getErrorPattern().matcher(errLine);
                if(errMatcher.matches()){
                    log.debug("ERROR MATCHES!");
                    errArr = new String[2];
                    errArr[0] = knownError.getErrorStatus();
                    if(knownError.getErrorMessage() != null){
                        errArr[1] = knownError.getErrorMessage();
                    }else if(errMatcher.groupCount() > 0){
                        errArr[1] = errMatcher.group(1);
                    }else{
                        errArr[1] = defaultErrorMsg;
                    }
                    errFound = true;
                    break;
                }
            }
            if(errFound){
                break;
            }
        }
        return errArr;
    }
    
    protected MeasurementResult buildErrorResult(String status, String message, Date start, Date end){
        MeasurementResult result = new MeasurementResult(new BasicDBObject());
        result.setStatus(status);
        result.setMessage(message);
        Date now = new Date();
        if(start == null || end == null){
            //if either null then set both to now to avoid srange results
            start = now;
            end = now;
        }
        result.setStartTime(start);
        result.setEndTime(end);
        return result;
    }
    
    abstract protected long getTimeout();

    abstract protected String getToolName();

    abstract protected void handleOutput(BufferedReader stdout, BufferedReader stderr, Schedule schedule) throws IOException;
    
    abstract protected void handleError(int resultCode, BufferedReader stdout, BufferedReader stderr, Schedule schedule) throws IOException;
    
    abstract protected void handleTimeout(Schedule schedule);
    
    protected void configureSSH(List<String> commandList, Schedule schedule){
        if(!this.sshConfig.isEnabled()){
           return;  
        }
        
        String host = null;
        for(String hostPref : this.sshConfig.getHostPrefs()){
            if(SSHConfig.HOST_LOCAL.equals(hostPref)){
                //not doing ssh
                return;
            }else if(hostPref.startsWith(SSHConfig.HOST_SSH_PREFIX)){
                host = hostPref.replaceFirst(SSHConfig.HOST_SSH_PREFIX, "");
                break;
            }else if(schedule.getDBObject().containsField(hostPref) && 
                    schedule.getDBObject().get(hostPref) != null){
                host = (String)schedule.getDBObject().get(hostPref);
                break;
            }
        }
        
        //check SSH host
        if(host == null){
            throw new RuntimeException("Unable to run command because no SSH host found");
        }
        try {
            InetAddress hostInet = InetAddress.getByName(host);
            if(hostInet.isLoopbackAddress()){
                //don't ssh into local address
                return;
            }
            InetAddress localInet = InetAddress.getByName(sshConfig.getLocalhost());
            if(localInet.getHostAddress().equals(hostInet.getHostAddress())){
                //this is the local host
                return;
            }
        } catch (UnknownHostException e) {
            log.warn("Unable to use ssh because invalid ssh host: " + e.getMessage());
            e.printStackTrace();
        }
        if(this.sshConfig.getScheduleHostField() != null){
            schedule.getDBObject().put(this.sshConfig.getScheduleHostField(), host);
        }
        //set command
        commandList.add(this.sshConfig.getCommand());
        
        //disable password prompts
        commandList.add("-o");
        commandList.add("PasswordAuthentication=no");
        
        //set key to -i
        if(this.sshConfig.getKey() != null){
            commandList.add("-i");
            commandList.add(this.sshConfig.getKey());
        }
        
        //set user to -l
        if(this.sshConfig.getUser() != null){
            commandList.add("-l");
            commandList.add(this.sshConfig.getUser());
        }
        
        //set the host
        commandList.add(host);
    }
    
    private class WatchDog extends Thread{
        private Process process;
        private Integer exit = null;

        public WatchDog(Process process){
            this.process = process;
        }

        public void run(){
            try {
                this.exit = this.process.waitFor();
            } catch (InterruptedException e) {
                return;
            }
        }
    }
}
