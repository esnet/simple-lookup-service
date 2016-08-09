package net.es.lookup.loadgen; /**
 * Wrapper class for Executor Service
 */

import java.util.concurrent.*;
import net.es.lookup.rmqmessages.KVGMessage;
/**
 * Created by kamal on 6/1/16.
 */
public class RequestorThreadPool
{

    private ExecutorService threadPool;

    public RequestorThreadPool()
    {
        threadPool =  Executors.newFixedThreadPool(Constants.NUMTHREADS);

    }

    public void sendRequest(KVGMessage message)
    {
        Requestor request = new Requestor(message);
        threadPool.execute(request);
    }

}
