package net.es.lookup.timer;

import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

/**
 * Author: sowmya
 * Date: 3/8/16
 * Time: 1:44 PM
 */
public class Scheduler {

    private org.quartz.Scheduler quartzScheduler;

    private static Scheduler instance;

    private Scheduler() {

        SchedulerFactory sf = new StdSchedulerFactory();

        try {
            this.quartzScheduler =sf.getScheduler();
            quartzScheduler.start();
        } catch (SchedulerException e) {
            new Exception("net.es.lookup.timer.Scheduler()"+e.getMessage());
        }
    }

    public static Scheduler getInstance(){

        if(instance != null){
            return instance;
        }else{
            createInstance();
            return instance;
        }

    }

    private static synchronized void createInstance() {

        if(instance == null){
            instance = new Scheduler();
        }
    }

    public void schedule(JobDetail quartzjob, Trigger trigger){

        try {
            quartzScheduler.scheduleJob(quartzjob, trigger);
        } catch (SchedulerException e) {
            new Exception("net.es.lookup.timer.Scheduler()"+e.getMessage());
        }

    }




}
