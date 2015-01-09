package net.es.lookup.pubsub;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.PubSubQueryException;
import net.es.lookup.common.exception.internal.PubSubQueueException;

import java.util.List;

/**
 *
 * This defines the interface that takes care of filling
 * the queues with the correct records.
 *
 * Author: sowmya
 * Date: 3/19/13
 * Time: 1:33 PM
 *
 */
public interface QueueDataGenerator {
    /**
     * This method assigns the records to queues.
     *
     * @param messageList The records that need to be sent to queue(s)
     * @return void
     */
    public void fillQueues(List<Message> messageList) throws PubSubQueueException, PubSubQueryException;


    /**
     * Push messages in temporary queue to the message exchange
     *
     */
    public void pushToQueue();



}
