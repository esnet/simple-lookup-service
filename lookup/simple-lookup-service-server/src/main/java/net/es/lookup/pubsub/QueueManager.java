package net.es.lookup.pubsub;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.PubSubQueryException;
import net.es.lookup.common.exception.internal.PubSubQueueException;

import java.util.List;

/**
 * This defines the interface that manages the pub-sub queues
 * for the lookup service.
 *
 * Author: sowmya
 * Date: 2/25/13
 * Time: 2:50 PM
 */
public interface QueueManager {

    /**
     * This method is used to retrieve the list of queues that
     * are assigned for a particular query. It creates one if none is found.
     *
     * @param query The lookup service query for which a queue is required
     * @return Returns a list of queue identifiers (string)
     */
    public List<String> getQueues(Message query) throws PubSubQueryException, PubSubQueueException;

    /**
     * This method returns true if one or more queues exists for a particular query
     *
     * @param query The lookup service query for which a queue is required
     * @return Returns true if one or more queue is found for the query
     */
    public boolean hasQueues(Message query) throws PubSubQueryException, PubSubQueueException;

    /**
     * This method pushes elements to queue
     *
     * @param qid     The queue id to which elements needs to be pushed to
     * @param messages
     * @return void
     */
    public void push(String qid, List<Message> messages) throws PubSubQueueException;

    /**
     * This method returns all the queries that the QueueManager is handling
     * @return List<String> A list of all the queries
     */
    public List<Message> getAllQueries();

}
