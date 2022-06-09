package net.es.lookup.cache.subscribe;

import com.rabbitmq.client.Consumer;

/**
 * Author: sowmya
 * Date: 8/8/16
 * Time: 4:58 PM
 */
public interface Subscriber extends Consumer{
    /**
     * Init method to complete the initialization of object
     *
     * */
    public void init();

    /**
     * This method creates the connection and starts the subscriber
     * This is a non-blocking method
     */
    public void start() throws SubscriberException;

    /**
     * This method  stops the subscriber
     */
    public void stop();
}
