package net.es.lookup.cache.dispatch;

import net.es.lookup.utils.config.reader.SubscriberConfigReader;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: sowmya
 * Date: 8/15/16
 * Time: 7:57 PM
 *
 * This class dispatches messages to the endpoints. This maintains a thread pool and schedules
 * the messages to be sent. This is a singleton instance as we want the total
 * threads to be centrally managed for this application.
 *
 */
public class DispatchService {

    /**
     * This class uses an ExecutorService to manage the client pool and schedule the threads.
     * The client pool can be configured when this singleton instance is first created. It is configured via a
     * parameter in the config file. After that it cannot be changed.
     * */
    private ExecutorService executorService;
    private static DispatchService instance = null;
    public static final int DEFAULT_POOL = 100;
    private int clientPool=DEFAULT_POOL;
    private static Logger LOG = Logger.getLogger(DispatchService.class);

    //private constructor as it is a singleton instance
    private DispatchService(){
        if (SubscriberConfigReader.getInstance().getThreadPool()>0){
            clientPool = SubscriberConfigReader.getInstance().getThreadPool();
        }
        executorService = Executors.newFixedThreadPool(clientPool);
    }

    /**
     * Returns an instance of this class if one is available. If not, creates a new instance and
     * returns it.
     * */
    public static DispatchService getInstance(){
        if(instance == null){
            createInstance();
        }
        return instance;
    }

    /**
     * This method handles the creation of the instance. This method is thread safe.
     * */
    private static synchronized void createInstance(){
        if(instance == null){
            instance = new DispatchService();
        }
    }

    /**
     * This method handles the scheduling of a thread to send the message to the given endpoint
     * @param  endPoint The destination to which the message has to be delivered
     * @param jsonMessage  The message to be sent
     * @return void
     * */
    public void schedule(EndPoint endPoint, JSONObject jsonMessage){
        executorService.submit(()->{
            LOG.info("Spooling thread to dispatch: "+Thread.currentThread().getId());
            endPoint.send(jsonMessage);
        });
    }

    /**
     * Returns the current thread pool size
     * */
    public int getClientPool() {
        return clientPool;
    }




}
