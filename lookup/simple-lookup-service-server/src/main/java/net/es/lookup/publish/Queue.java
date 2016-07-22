package net.es.lookup.publish;

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




    private Message query;



    public Message getQuery() {

        return query;
    }

    public void setQuery(Message query) {

        this.query = query;
    }

    /**
     * This method is used to push messages to the queue, which also includes pushing the message to the client
     *
     * @param messages The message that will be pushed to the queue and subsequently to the client
     * @return void This method returns void. In case of errors, an exception is thrown
     * */
    public abstract void push(List<Message> messages) throws PubSubQueueException;


    /**
     * This method is used to push messages to the queue, which also includes pushing the message to the client
     *
     * @param message The message that will be pushed to the queue and subsequently to the client
     * @return void This method returns void. In case of errors, an exception is thrown
     * */
    public abstract void push(Message message) throws PubSubQueueException;


    /**
     * This method is used to push messages to the queue, which also includes pushing the message to the client
     *
     * @param message The message that will be pushed to the queue and subsequently to the client
     * @return void This method returns void. In case of errors, an exception is thrown
     * */
    public abstract void push(String message) throws PubSubQueueException;


    /**
     * This method retrieves the id of the queue
     *
     * @return String  The id of the queue is expected to be a string
     * */
    public abstract String getQid();

    /**
     * This method retrieves the accesspoint of the queue
     *
     * @return String  The accesspoint of the queue
     * */
    public abstract String getQAccessPoint();


}
