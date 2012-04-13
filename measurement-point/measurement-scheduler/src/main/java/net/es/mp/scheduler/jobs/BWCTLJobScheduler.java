package net.es.mp.scheduler.jobs;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import net.es.mp.authz.AuthzConditions;
import net.es.mp.scheduler.MPSchedulingService;
import net.es.mp.scheduler.MPSchedulerException;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.scheduler.types.BWCTLSchedule;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.scheduler.types.validators.BWCTLScheduleValidator;
import net.es.mp.util.publishers.LocalStreamPublisher;
import net.es.mp.util.publishers.Publisher;

public class BWCTLJobScheduler implements MPJobScheduler{
    private Logger log = Logger.getLogger(BWCTLJobScheduler.class);
    private Logger netlogger = Logger.getLogger("netlogger");
    private BWCTLScheduleValidator validator;
    private Publisher streamPublisher;
    
    final private String SUBMIT_EVENT = "mp.scheduler.BWCTLJobScheduler.submit";
    final private String COMMIT_EVENT = "mp.scheduler.BWCTLJobScheduler.commit";
    
    public BWCTLJobScheduler(){
        this.validator = new BWCTLScheduleValidator();
        this.streamPublisher = new LocalStreamPublisher();
    }
    
    public URI submitSchedule(Schedule schedule, AuthzConditions authzConditions) throws MPSchedulerException{
        NetLogger netLog = NetLogger.getTlogger();
        this.netlogger.debug(netLog.start(SUBMIT_EVENT));
        BWCTLSchedule bwctlSchedule = new BWCTLSchedule(schedule.getDBObject());
        try{
            this.validator.validate(bwctlSchedule);
        }catch(Exception e){
            throw new MPSchedulerException(e.getMessage());
        }
        
        //create stream
        URI streamURI = this.streamPublisher.create(bwctlSchedule);
        
        this.netlogger.debug(netLog.end(SUBMIT_EVENT));
        return  streamURI;
    }
    
    public void commitSchedule(Schedule schedule, AuthzConditions authzConditions) throws MPSchedulerException{
        NetLogger netLog = NetLogger.getTlogger();
        this.netlogger.debug(netLog.start(COMMIT_EVENT));
        String jobKey =  UUID.randomUUID().toString();
        String triggerName = "bwctlTrigger-" + jobKey;
        String jobName = "bwctlJob-" + jobKey;
        SimpleTrigger trigger = new SimpleTrigger(triggerName, null, 
                new Date(), null, 0, 0L);
        JobDetail jobDetail = new JobDetail(jobName, "BWCTL",
                BWCTLJob.class);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("schedule", new BWCTLSchedule(schedule.getDBObject()));
        dataMap.put("authzConditions", authzConditions);
        jobDetail.setJobDataMap(dataMap);
        try {
            MPSchedulingService.getInstance().getThreadScheduler().scheduleJob(jobDetail, trigger);
            System.out.println("Started: " + MPSchedulingService.getInstance().getThreadScheduler().isStarted());
        } catch (SchedulerException e) {
            this.netlogger.debug(netLog.error(COMMIT_EVENT, e.getMessage()));
            this.log.error(e.getMessage());
            e.printStackTrace();
            throw new MPSchedulerException(e.getMessage());
        }
        this.netlogger.debug(netLog.end(COMMIT_EVENT));
    }
}
