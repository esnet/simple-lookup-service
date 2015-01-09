package net.es.lookup.service;

import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.pubsub.client.Cache;
import org.apache.log4j.Logger;

import java.util.List;

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

    private CacheService(List<Cache> caches) throws LSClientException {

        if (caches != null && !caches.isEmpty()) {
            this.cacheList = caches;
            initialized = true;
            LOG.debug("net.es.lookup.service.CacheService: Number of caches - " + cacheList.size());

        }
    }

    public static synchronized CacheService getInstance() {

        return instance;

    }

    public List<Cache> getCacheList() {

        return cacheList;
    }

    public static synchronized CacheService initialize(List<Cache> cacheList) throws LSClientException {

        if (instance != null) {
            throw new RuntimeException("Attempt to create second instance");
        } else {
            instance = new CacheService(cacheList);
        }
        return instance;
    }

    public boolean isInitialized() {

        return initialized;
    }

    public void startService() {

        LOG.debug("net.es.lookup.service.CacheService: starting cache");


        for (Cache cache : cacheList) {
            try {
                cache.start();
            } catch (LSClientException e) {
                LOG.error("net.es.lookup.service.CacheService: Error starting cache- " + cache.getName());

            }
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
