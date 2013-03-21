package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.pubsub.QueuePump;

import java.util.List;

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


        Message query = null;
        List<Message> queries = amQueueManager.getAllQueries();

        if(queries != null && !queries.isEmpty()){
            query = queries.get(0);
        }

        if(query !=null){
            String qid = amQueueManager.getQueues(query).get(0);
            if(qid != null && !qid.isEmpty()){
                for(int i=0; i< messageList.size(); i++){
                    amQueueManager.push(qid,messageList.get(i));
                }
            }

        }

    }

}
