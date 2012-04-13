package net.es.mp.scheduler.jobs;

import java.net.URI;
import java.util.Date;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import net.es.mp.authz.AuthzConditions;
import net.es.mp.scheduler.MPSchedulerException;
import net.es.mp.scheduler.MPSchedulingService;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.scheduler.types.OWAMPSchedule;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.scheduler.types.validators.OWAMPScheduleValidator;
import net.es.mp.util.publishers.LocalStreamPublisher;
import net.es.mp.util.publishers.Publisher;

public class OWAMPJobScheduler implements MPJobScheduler{
    private Logger log = Logger.getLogger(OWAMPJobScheduler.class);
    private Logger netlogger = Logger.getLogger("netlogger");
    private OWAMPScheduleValidator validator;
    private Publisher streamPublisher;

    final private String SUBMIT_EVENT = "mp.scheduler.OWAMPJobScheduler.submit";
    final private String COMMIT_EVENT = "mp.scheduler.OWAMPJobScheduler.commit";

    public OWAMPJobScheduler(){
        this.validator = new OWAMPScheduleValidator();
        this.streamPublisher = new LocalStreamPublisher();
    }

    public URI submitSchedule(Schedule schedule, AuthzConditions authzConditions)
            throws MPSchedulerException {
        NetLogger netLog = NetLogger.getTlogger();
        this.netlogger.debug(netLog.start(SUBMIT_EVENT));
        OWAMPSchedule owampSchedule = new OWAMPSchedule(schedule.getDBObject());
        try{
            this.validator.validate(owampSchedule);
        }catch(Exception e){
            throw new MPSchedulerException(e.getMessage());
        }

        //create stream
        URI streamURI = this.streamPublisher.create(owampSchedule);

        this.netlogger.debug(netLog.end(SUBMIT_EVENT));
        return  streamURI;
    }

    public void commitSchedule(Schedule schedule,
            AuthzConditions authzConditions) throws MPSchedulerException {
        
        NetLogger netLog = NetLogger.getTlogger();
        this.netlogger.debug(netLog.start(COMMIT_EVENT));
        String jobKey =  UUID.randomUUID().toString();
        String triggerName = "owampTrigger-" + jobKey;
        String jobName = "owampJob-" + jobKey;
        SimpleTrigger trigger = new SimpleTrigger(triggerName, null, 
                new Date(), null, 0, 0L);
        JobDetail jobDetail = new JobDetail(jobName, "OWAMP",
                OWAMPJob.class);
        JobDataMap dataMap = new JobDataMap();
        dataMap.put("schedule", new OWAMPSchedule(schedule.getDBObject()));
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
