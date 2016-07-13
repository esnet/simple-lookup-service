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


    private String userName;
    private String password;
    private String vhost;
    private int port = 5672;


    private static boolean serviceOn=false;

    private final int DEFAULT_SCHEDULER_INTERVAL=30;

    private int schedulerInterval=DEFAULT_SCHEDULER_INTERVAL;

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

    public int getSchedulerInterval() {

        return schedulerInterval;
    }

    public void setSchedulerInterval(int schedulerInterval) {

        this.schedulerInterval = schedulerInterval;
    }

    public static boolean isServiceOn() {

        return serviceOn;
    }

    public String getUserName() {

        return userName;
    }

    public void setUserName(String userName) {

        this.userName = userName;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }

    public String getVhost() {

        return vhost;
    }

    public void setVhost(String vhost) {

        this.vhost = vhost;
    }

    public int getPort() {

        return port;
    }

    public void setPort(int port) {

        this.port = port;
    }

    public void startService(){
        serviceOn=true;
        System.out.println("Starting publisher with host="+host+" maxPushEvents="+maxPushEvents + " maxInterval="+maxInterval);

        try {

            RMQueue rmQueue = new RMQueue(host,port,userName,password,vhost,maxPushEvents,maxInterval);
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
                            .withIntervalInSeconds(schedulerInterval)
                            .repeatForever()
                            .withMisfireHandlingInstructionIgnoreMisfires())
                    .build();

            Scheduler.getInstance().schedule(publisherScheduler, psTrigger);

            createdPublishJob = true;
        }

    }

    //TODO:Create a stop method

}
