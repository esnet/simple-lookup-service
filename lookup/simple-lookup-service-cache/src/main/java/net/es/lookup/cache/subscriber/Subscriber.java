package net.es.lookup.cache.subscriber;

import java.util.List;

/**
 * Author: sowmya
 * Date: 3/29/16
 * Time: 10:45 AM
 */
public abstract class Subscriber {

    private String host;
    private int port;
    private List<String> queries;

    public Subscriber(String host, int port, List<String> queries) {
        this.host= host;
        this.port=port;
        this.queries = queries;
    }


    public String getHost() {

        return host;
    }

    public int getPort() {

        return port;
    }

    public List<String> getQueries() {

        return queries;
    }

    /**
     * Init method to complete the initialization of object
     *
     * */


    public abstract void init();

    /**
     * This method creates the connection and starts the subscriber
     * This is a non-blocking method
     */
    public abstract void start() throws SubscriberException;

    /**
     * This method  stops the subscriber
     */
    public abstract void stop();


}
