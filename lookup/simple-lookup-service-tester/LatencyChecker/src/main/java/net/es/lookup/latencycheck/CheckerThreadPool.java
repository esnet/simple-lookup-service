package net.es.lookup.latencycheck;

import net.es.lookup.rmqmessages.LGMessage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kamala on 6/2/16.
 */
public class CheckerThreadPool
{
    private static final int NUMTHREADS = 2000;

    private ExecutorService threadPool;

    public CheckerThreadPool()
    {
        threadPool =  Executors.newFixedThreadPool(NUMTHREADS);
    }

    public void checkLatency(LGMessage message)
    {
        Checker request = new Checker(message);
        threadPool.execute(request);
    }


}
