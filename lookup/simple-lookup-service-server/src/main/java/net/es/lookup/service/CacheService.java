package net.es.lookup.service;

import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.pubsub.client.Cache;
import net.es.lookup.pubsub.client.heartbeat.CacheHeartBeat;
import org.apache.log4j.Logger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Author: sowmya
 * Date: 3/5/14
 * Time: 6:17 PM
 */
public class CacheService {

    private List<Cache> cacheList;
    private static CacheService instance = null;
    private static Logger LOG = Logger.getLogger(CacheService.class);
    private static boolean initialized = false;
    private Scheduler scheduler;
    private static final int FAILURE_RECOVERY_INTERVAL = 600;

    private CacheService(List<Cache> caches, Scheduler scheduler) throws LSClientException {

        if(caches != null && !caches.isEmpty() && scheduler != null){
            this.cacheList = caches;
            initialized=true;
            this.scheduler = scheduler;
            LOG.debug("net.es.lookup.service.CacheService: Number of caches - "+ cacheList.size());
        }

    }

    public static synchronized CacheService getInstance() {

        return instance;

    }

    public List<Cache>getCacheList(){
        return cacheList;
    }

    public static synchronized CacheService initialize(List<Cache> cacheList, Scheduler scheduler) throws LSClientException {

        if (instance != null) {
            throw new RuntimeException("Attempt to create second instance");
        } else {
            instance = new CacheService(cacheList, scheduler);
        }
        return instance;
    }

    public boolean isInitialized(){
        return initialized;
    }

    public void startService() {

        LOG.debug("net.es.lookup.service.CacheService: starting cache");
            try {
                JobDetail job = newJob(CacheHeartBeat.class)
                        .withIdentity("failure-handler", "CacheHeartBeat")
                        .build();

                // Trigger the job to run now, and then every dbpruneInterval seconds
                Trigger trigger = newTrigger().withIdentity("failure-handler-trigger", "CacheHeartBeat")
                        .startNow()
                        .withSchedule(simpleSchedule()
                                .withIntervalInSeconds(FAILURE_RECOVERY_INTERVAL)
                                .repeatForever()
                                .withMisfireHandlingInstructionIgnoreMisfires())
                        .build();

                this.scheduler.scheduleJob(job, trigger);

                for(Cache cache: cacheList){
                    try {
                        cache.start();
                    } catch (LSClientException e) {
                        LOG.error("net.es.lookup.service.CacheService: Error starting cache- " + cache.getName());

                    }
                }

           } catch (SchedulerException e) {
                LOG.error("net.es.lookup.service.CacheService: Cannot start failure handler");
            }

    }

    public void stopService() {

        for (Cache cache : cacheList) {
            try {
                cache.stop();
            } catch (LSClientException e) {
                LOG.error("net.es.lookup.service.CacheService: Error starting cache- " + cache.getName());
            }
        }
    }


}
