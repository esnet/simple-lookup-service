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
 * <p/>
 * Author: sowmya
 * Date: 3/1/13
 * Time: 3:12 PM
 */
public class AMQueueManager implements QueueManager {

    private HashMap<String, AMQueue> queueMap = new HashMap<String, AMQueue>();            /* keeps track of queueid to queue mapping */
    private HashMap<String, List<String>> queryMap = new HashMap<String, List<String>>();   /* keeps track of query to queueid mapping  */
    private HashMap<String, List<Message>> normalizedQueryMap = new HashMap<String, List<Message>>();   /* keeps track of normalized query to original query mapping  */

    private static AMQueueManager instance = null;
    private static Logger LOG = Logger.getLogger(AMQueueManager.class);

    private AMQueueManager() {

        setInstance(this);
    }

    public static synchronized void setInstance(AMQueueManager amQueueManager) {

        if (instance != null) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueueManager.setInstance: Attempting to create second AMQueueManager. So throwing RuntimeException");
            throw new RuntimeException("net.es.lookup.pubsub.amq.AMQueueManager.setInstance: Attempt to create second instance");
        } else {
            instance = amQueueManager;
        }
    }

    public static AMQueueManager getInstance() {
        if(instance ==null){
            new AMQueueManager();
        }
        return instance;
    }

    public boolean isUp(){
        if(instance != null){
            return true;
        }else{
            return false;
        }
    }

    /**
     * This is the implementation of the getQueues method declared in the QueueManager interface.
     * The method normalizes the query and searches if queue exists for the query. If queue exists,
     * the queue id is returned. Else, a queue is created and the id is returned.
     */
    public List<String> getQueues(Message query) throws QueryException, QueueException {

        LOG.info("net.es.lookup.pubsub.amq.AMQueueManager.getQueues: Creating/Retrieving Queues");
        List<String> res = new ArrayList<String>();
        String normalizedQuery = "";
        LOG.info("net.es.lookup.pubsub.amq.AMQueueManager.getQueues: Calling Normalize query - ");
        normalizedQuery = QueryNormalizer.normalize(query);
        LOG.info("net.es.lookup.pubsub.amq.AMQueueManager.getQueues: Normalized query - "+ normalizedQuery);
        if (!normalizedQuery.isEmpty()) {

            if (queryMap.containsKey(normalizedQuery)) {

                res = queryMap.get(normalizedQuery);
                LOG.debug("net.es.lookup.pubsub.amq.AMQueueManager.getQueues: Queue exists. ");

            } else {

                System.out.println(queryMap.toString());
                AMQueue queue = new AMQueue();
                String qid = queue.getQid();
                LOG.debug("net.es.lookup.pubsub.amq.AMQueueManager.getQueues: Created queue with id " + qid);


                //add queue to queueMap
                queueMap.put(qid, queue);

                //add to queryMap
                res.add(qid);
                queryMap.put(normalizedQuery, res);

                //add to normalized query
                List<Message> queryList = new ArrayList<Message>();
                queryList.add(query);
                normalizedQueryMap.put(normalizedQuery, queryList);
            }

        }
        LOG.debug("net.es.lookup.pubsub.amq.AMQueueManager.getQueues: Returning - " + res);
        return res;

    }

    /**
     * This method is the implementation of the hasQueues method declared by QueueManager interface.
     * This method simply returns true if queue exists for a query and false if not .
     */
    public boolean hasQueues(Message query) throws QueryException, QueueException {

        String normalizedQuery = QueryNormalizer.normalize(query);
        if (queryMap.containsKey(normalizedQuery)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * This method is the implementation of the push method declared by QueueManager interface.
     * This method simply checks if queue exists and pushes the message to the queue. If queue does
     * not exist, it throws a QueueException.
     */
    public void push(String qid, Message message) throws QueueException {

        LOG.info("net.es.lookup.pubsub.amq.AMQueueManager.push: Pushing Message" + message + " to Queue " + qid);
        AMQueue queue = queueMap.get(qid);

        if (queue != null) {
            queue.push(message);
            LOG.debug("net.es.lookup.pubsub.amq.AMQueueManager.push: Pushed Message" + message + " to Queue " + qid);
        } else {
            LOG.error("net.es.lookup.pubsub.amq.AMQueueManager.push: Error pushing message to Queue. Queue does not exist");
            throw new QueueException("Queue does not exist");
        }

    }

    public List<Message> getAllQueries() {
        LOG.debug("net.es.lookup.pubsub.amq.AMQueueManager.getAllQueries: Retrieving all queries handled by Subscribe");
        List<Message> queryList = new ArrayList<Message>();
        for (String q : queryMap.keySet()) {
            queryList.addAll(normalizedQueryMap.get(q));
        }
        LOG.debug("net.es.lookup.pubsub.amq.AMQueueManager.getAllQueries: Retrieved Queries - "+ queryList.size());
        return queryList;
    }

}
