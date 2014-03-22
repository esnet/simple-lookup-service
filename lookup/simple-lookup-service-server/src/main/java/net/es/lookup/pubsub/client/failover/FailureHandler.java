package net.es.lookup.pubsub.client.failover;

import net.es.lookup.pubsub.client.Cache;
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
public class FailureHandler implements Job {

    private static Logger LOG = Logger.getLogger(FailureHandler.class);
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        CacheService cacheService = CacheService.getInstance();
        List<Cache> cacheList = cacheService.getCacheList();
        LOG.debug("net.es.lookup.service.CacheService: starting cache");
        for (Cache cache : cacheList) {
            FailureRecovery failureRecovery = cache.getFailureRecovery();
            failureRecovery.execute();
        }
        }
    }

