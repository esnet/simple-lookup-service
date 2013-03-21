package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.QueryNormalizer;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.pubsub.QueueManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * This class implements the QueueManager interface. This implementation has a 1 to 1 mapping between query and queue.
 *
 * Author: sowmya
 * Date: 3/1/13
 * Time: 3:12 PM
 */
public class AMQueueManager implements QueueManager {

    private HashMap<String, AMQueue> queuemap = new HashMap<String, AMQueue >();            /* keeps track of queueid to queue mapping */
    private HashMap<String, List<String>> querymap = new HashMap<String, List<String>>();   /* keeps track of query to queueid mapping  */
    private HashMap<String, List<Message>> normalizedquerymap = new HashMap<String, List<Message>>();   /* keeps track of normalized query to original query mapping  */

    private static AMQueueManager instance = null;
    private static Logger LOG = Logger.getLogger(AMQueueManager.class);

    public AMQueueManager() {
        setInstance(this);
    }

    public static synchronized void setInstance(AMQueueManager amQueueManager){
        if(instance != null){
            throw new RuntimeException("net.es.lookup.pubsub.amq.AMQueueManager: Attempt to create second instance");
        }else{
            instance = amQueueManager;
        }
    }

    public static AMQueueManager getInstance(){
        return instance;
    }

    /**
     * This is the implementation of the getQueues method declared in the QueueManager interface.
     * The method normalizes the query and searches if queue exists for the query. If queue exists,
     * the queue id is returned. Else, a queue is created and the id is returned.
     * */
    public List<String> getQueues(Message query) throws QueryException, QueueException  {

        List<String> res = new ArrayList<String>();
        String normalizedQuery = "";
        normalizedQuery = QueryNormalizer.normalize(query);

        if(!normalizedQuery.isEmpty()){

             if(querymap.containsKey(normalizedQuery)){

                 res = querymap.get(normalizedQuery);
                 LOG.info("net.es.lookup.pubsub.amq.AMQueueManager: Queue exists. ");

             }else{

                 System.out.println(querymap.toString());
                 AMQueue queue = new AMQueue();
                 String qid = queue.getQid();
                 LOG.info("net.es.lookup.pubsub.amq.AMQueueManager: Created queue with id "+qid);


                 //add queue to queuemap
                 queuemap.put(qid, queue);

                 //add to querymap
                 res.add(qid);
                 querymap.put(normalizedQuery, res);

                 //add to normalized query
                 List<Message> queryList = new ArrayList<Message>();
                 queryList.add(query);
                 normalizedquerymap.put(normalizedQuery,queryList);
             }

        }

        return res;

    }

    /**
     * This method is the implementation of the hasQueues method declared by QueueManager interface.
     * This method simply returns true if queue exists for a query and false if not .
     * */
    public boolean hasQueues(Message query) throws QueryException, QueueException {

        String normalizedquery = QueryNormalizer.normalize(query);
        if(querymap.containsKey(normalizedquery)){
            return true;
        }else{
            return false;
        }
    }


    /**
    * This method is the implementation of the push method declared by QueueManager interface.
    * This method simply checks if queue exists and pushes the message to the queue. If queue does
    * not exist, it throws a QueueException.
    * */
    public void push(String qid, Message message) throws QueueException {

        AMQueue queue = queuemap.get(qid);

        if (queue != null) {
            queue.push(message);
        } else{
            throw new QueueException("Queue does not exist");
        }

    }

        public List<Message> getAllQueries(){

        List<Message> queryList = new ArrayList<Message>();

        for(String q: querymap.keySet()){
            queryList.addAll(normalizedquerymap.get(q));
        }

        return queryList;
    }

}
