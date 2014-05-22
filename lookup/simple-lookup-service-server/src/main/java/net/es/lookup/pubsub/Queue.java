package net.es.lookup.pubsub;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.PubSubQueueException;

import java.util.List;

/**
 * This abstract class defines a generic pub-sub queue.
 *
 * Author: sowmya
 * Date: 2/28/13
 * Time: 1:25 PM
 */
public abstract class Queue {
    /**
     * This method is used to push messages to the queue, which also includes pushing the message to the client
     *
     * @param messages The message that will be pushed to the queue and subsequently to the client
     * @return void This method returns void. In case of errors, an exception is thrown
     * */
    public abstract void push(List<Message> messages) throws PubSubQueueException;


    /**
     * This method retrieves the id of the queue
     *
     * @return String  The id of the queue is expected to be a string
     * */
    public abstract String getQid();


}
