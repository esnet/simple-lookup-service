package net.es.lookup.service;

import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.publish.Publisher;
import net.es.lookup.publish.PublisherScheduler;
import net.es.lookup.publish.rabbitmq.RMQueue;
import net.es.lookup.timer.Scheduler;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Author: sowmya
 * Date: 3/17/16
 * Time: 11:58 AM
 */
public class PublishService {

    private static PublishService instance = null;
    private boolean createdPublishJob = false;

    private String host;
    private long maxInterval;
    private int maxPushEvents;

    public String getHost() {

        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public long getMaxInterval() {

        return maxInterval;
    }

    public void setMaxInterval(long maxInterval) {

        this.maxInterval = maxInterval;
    }

    public int getMaxPushEvents() {

        return maxPushEvents;
    }

    public void setMaxPushEvents(int maxPushEvents) {

        this.maxPushEvents = maxPushEvents;
    }

    public int getSCHEDULER_INTERVAL() {

        return SCHEDULER_INTERVAL;
    }

    private final int SCHEDULER_INTERVAL=30;


    private PublishService(){
    }

    public static PublishService getInstance(){
        if(instance == null){
            createInstance();
        }
        return instance;
    }

    private static synchronized void createInstance(){
        if (instance == null){
            instance = new PublishService();
        }
    }

    public void startService(){
        System.out.println("Starting publisher with host="+host+" maxPushEvents="+maxPushEvents + " maxInterval="+maxInterval);

        try {

            RMQueue rmQueue = new RMQueue(host,maxPushEvents,maxInterval);
            String query = "all";
            Publisher.getInstance().addQueue(query,rmQueue);

        } catch (PubSubQueueException e) {
            e.printStackTrace();
        } catch (DuplicateEntryException e) {
            e.printStackTrace();
        }



        createPublishJob();

    }

    private synchronized void createPublishJob() {
        if (!createdPublishJob){
            System.out.println("Created publisher scheduler job");

            JobDetail publisherScheduler = newJob(PublisherScheduler.class)
                    .withIdentity("publisher_Scheduler", "pubsub")
                    .build();


            Trigger psTrigger = newTrigger().withIdentity("pstrigger", "pubsub")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInSeconds(SCHEDULER_INTERVAL)
                            .repeatForever()
                            .withMisfireHandlingInstructionIgnoreMisfires())
                    .build();

            Scheduler.getInstance().schedule(publisherScheduler, psTrigger);

            createdPublishJob = true;
        }

    }


}
