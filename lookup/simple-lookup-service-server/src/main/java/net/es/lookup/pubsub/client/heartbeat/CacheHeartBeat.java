package net.es.lookup.pubsub.client.heartbeat;

import net.es.lookup.client.Subscriber;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.pubsub.client.Cache;
import net.es.lookup.pubsub.client.failover.FailureRecovery;
import net.es.lookup.records.PubSub.SubscribeRecord;
import net.es.lookup.service.CacheService;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * Author: sowmya
 * Date: 3/21/14
 * Time: 4:03 PM
 */
public class CacheHeartBeat implements Job {

    private static Logger LOG = Logger.getLogger(CacheHeartBeat.class);
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        CacheService cacheService = CacheService.getInstance();
        List<Cache> cacheList = cacheService.getCacheList();
        LOG.debug("net.es.lookup.service.CacheService: Cache Heartbeat");
        boolean restartRequired = false;
        for (Cache cache : cacheList) {
            List<Subscriber> subscribers = cache.getSubscribers();
            for(Subscriber subscriber: subscribers){
                try {
                    SubscribeRecord record = subscriber.heartbeat();

                    if(record.getQueueState().equals(ReservedValues.RECORD_SUBSRIBER_QUEUE_STATE_NEW)){
                        LOG.error("Queue is new so restart required");
                        restartRequired=true;
                        break;

                    }else{
                        LOG.error("No restart required");
                    }
                } catch (LSClientException e) {
                    LOG.error("Heartbeat message Failed"+ e.getMessage());
                }
            }

            if(restartRequired){
                try {
                    cache.restart();
                } catch (LSClientException e) {
                    LOG.error("Cache restart failed"+ e.getMessage());
                }
            }
            FailureRecovery failureRecovery = cache.getFailureRecovery();
            failureRecovery.execute();
        }
        }
    }

