package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.pubsub.QueuePump;
import org.apache.log4j.Logger;

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

    private static Logger LOG = Logger.getLogger(AMQueuePump.class);


    public AMQueuePump() {
        setInstance(this);
        amQueueManager = AMQueueManager.getInstance();
    }


    public static synchronized void setInstance(AMQueuePump amQueuePump){
        if(instance != null){
            LOG.error("net.es.lookup.pubsub.amq.AMQueuePump.setInstance: Attempting to create second AMQueuePump. So throwing RuntimeException");
            throw new RuntimeException("net.es.lookup.pubsub.amq.AMQueuePump.setInstance: Attempt to create second instance");
        }else{
            instance = amQueuePump;
        }
    }


    public static AMQueuePump getInstance(){
        return instance;
    }


    public void fillQueues(List<Message> messageList) throws QueueException, QueryException {
        LOG.info("net.es.lookup.pubsub.amq.AMQueuePump.fillQueues: Filling up queues with message");
        List<Message> queries = amQueueManager.getAllQueries();

        //optimized for queries with only 1 key-value pair
        for (Message query : queries){
            Map<String,Object> queryMap = query.getMap();
            for (String key: queryMap.keySet()){
                for (Message message : messageList){
                    if (message.hasKey(key) && message.getKey(key).equals(queryMap.get(key))){
                        LOG.debug("net.es.lookup.pubsub.amq.AMQueuePump.fillQueues: Message "+message.getMap()+" mapped to query"+ query.getMap());
                        List <String> qids = amQueueManager.getQueues(query);

                        for(String qid: qids){
                            LOG.debug("net.es.lookup.pubsub.amq.AMQueuePump.fillQueues: Message "+message.getMap()+" mapped to queue"+ qid);
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
