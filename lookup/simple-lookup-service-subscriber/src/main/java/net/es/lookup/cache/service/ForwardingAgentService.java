package net.es.lookup.cache.service;

import net.es.lookup.cache.agent.Destination;
import net.es.lookup.cache.agent.ForwardingAgentFactory;
import net.es.lookup.utils.config.reader.SubscriberConfigReader;
import net.sf.json.JSONObject;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: sowmya
 * Date: 4/1/16
 * Time: 1:06 PM
 */
public class ForwardingAgentService {

    private ExecutorService executorService;
    private static ForwardingAgentService instance = null;
    public static final int DEFAULT_POOL = 100;



    private int clientPool=DEFAULT_POOL;



    private ForwardingAgentService(){
        if (SubscriberConfigReader.getInstance().getThreadPool()>0){
            clientPool = SubscriberConfigReader.getInstance().getThreadPool();
        }
        executorService = Executors.newFixedThreadPool(clientPool);
    }

    public static ForwardingAgentService getInstance(){
        if(instance == null){
            createInstance();

        }
        return instance;

    }

    private static synchronized void createInstance(){
        if(instance == null){
            instance = new ForwardingAgentService();
        }
    }

    public void schedule(Destination destination, JSONObject data){

        executorService.execute(ForwardingAgentFactory.createForwardingAgent(destination,data));

    }

    public int getClientPool() {

        return clientPool;
    }





}
