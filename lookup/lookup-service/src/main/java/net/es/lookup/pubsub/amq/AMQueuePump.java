package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.pubsub.QueuePump;

import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 3/19/13
 * Time: 1:44 PM
 */
public class AMQueuePump implements QueuePump {

    private static AMQueuePump instance = null;
    private AMQueueManager amQueueManager;


    public AMQueuePump() {
        setInstance(this);
        amQueueManager = AMQueueManager.getInstance();
    }


    public static synchronized void setInstance(AMQueuePump amQueuePump){
        if(instance != null){
            throw new RuntimeException("net.es.lookup.pubsub.amq.AMQueueManager: Attempt to create second instance");
        }else{
            instance = amQueuePump;
        }
    }


    public static AMQueuePump getInstance(){
        return instance;
    }


    public void fillQueues(List<Message> messageList) throws QueueException, QueryException {

        List<Message> queries = amQueueManager.getAllQueries();

        //optimized for queries with only 1 key-value pair
        for (Message query : queries){
            Map<String,Object> queryMap = query.getMap();
            for (String key: queryMap.keySet()){
                for (Message message : messageList){
                    if (message.hasKey(key) && message.getKey(key).equals(queryMap.get(key))){
                        System.out.println("Found a queue with key value");
                        List <String> qids = amQueueManager.getQueues(query);

                        for(String qid: qids){
                            if(qid != null && !qid.isEmpty()){
                                    amQueueManager.push(qid,message);
                            }
                        }

                    }
                }
            }
        }

    }

}
