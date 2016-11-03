package net.es.lookup.service;

import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.publish.Publisher;
import net.es.lookup.publish.PublisherScheduler;
import net.es.lookup.publish.rabbitmq.RMQueue;
import net.es.lookup.timer.Scheduler;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Trigger;

import java.util.Date;

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


    private String exchangeName;
    private String exchangeType;
    private boolean exchangeDurability;


    private static boolean serviceOn=false;

    private final int DEFAULT_SCHEDULER_INTERVAL=30;

    private int pollingInterval =DEFAULT_SCHEDULER_INTERVAL;

    private static Logger LOG = Logger.getLogger(PublishService.class);

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

    public int getPollingInterval() {

        return pollingInterval;
    }

    public void setPollingInterval(int pollingInterval) {

        this.pollingInterval = pollingInterval;
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

    public boolean isExchangeDurability() {

        return exchangeDurability;
    }

    public void setExchangeDurability(boolean exchangeDurability) {

        this.exchangeDurability = exchangeDurability;
    }

    public String getExchangeType() {

        return exchangeType;
    }

    public void setExchangeType(String exchangeType) {

        this.exchangeType = exchangeType;
    }

    public String getExchangeName() {

        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {

        this.exchangeName = exchangeName;
    }

    public void startService(){
        serviceOn=true;
        LOG.info(this.getClass().getName()+" Starting publisher with host="+host+" maxPushEvents="+maxPushEvents + " maxInterval="+maxInterval);

        try {

            RMQueue rmQueue = new RMQueue(host,port,userName,password,vhost, exchangeName, exchangeType, exchangeDurability);
            //for now only one queue is supported. Hence the hard coded value.
            String query = "all";
            Publisher publisher = Publisher.getInstance();
            publisher.addQueue(query,rmQueue);
            publisher.setCurrentPushEvents(0);
            publisher.setMaxPushEvents(maxPushEvents);
            publisher.setMaxPushInterval(maxInterval);
            publisher.setLastPushed(new Date());
            publisher.setPollInterval(pollingInterval);

        } catch (PubSubQueueException e) {
            LOG.error(this.getClass().getName()+"Error creating queue" + e.toString());
        } catch (DuplicateEntryException e) {
            LOG.error(this.getClass().getName()+"Queue already exists" + e.toString());
        }
        createPublishJob();

    }

    private synchronized void createPublishJob() {
        if (!createdPublishJob){
            LOG.info(this.getClass().getName()+" Created publisher job instance");


            JobDetail publisherScheduler = newJob(PublisherScheduler.class)
                    .withIdentity("publisher_Scheduler", "pubsub")
                    .build();




            Trigger psTrigger = newTrigger().withIdentity("pstrigger", "pubsub")
                    .startNow()
                    .withSchedule(simpleSchedule()
                            .withIntervalInMilliseconds(pollingInterval)
                            .repeatForever()
                            .withMisfireHandlingInstructionIgnoreMisfires())
                    .build();

            Scheduler.getInstance().schedule(publisherScheduler, psTrigger);

            createdPublishJob = true;
        }

    }

    //TODO:Create a stop method

}
