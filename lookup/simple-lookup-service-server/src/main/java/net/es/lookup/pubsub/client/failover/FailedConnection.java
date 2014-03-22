package net.es.lookup.pubsub.client.failover;

import net.es.lookup.client.Subscriber;
import net.es.lookup.common.exception.LSClientException;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.tools.JavaFileManager;
import java.util.Date;

/**
 * Author: sowmya
 * Date: 12/18/13
 * Time: 10:45 PM
 */
public class FailedConnection {

    private Subscriber subscriber;
    private long timeOfInitialFailure;
    private long timeOfLastFailure;
    private int reconnectionAttempts;

    public static final int MAX_RECONNECTION_ATTEMPTS = 10;


    public FailedConnection(Subscriber subscriber){
        this.subscriber = subscriber;
        long now = new Date().getTime();
        timeOfInitialFailure=now;
        timeOfLastFailure=now;
        reconnectionAttempts=0;
    }

    public FailedConnection(){
        long now = new Date().getTime();
        timeOfInitialFailure=now;
        timeOfLastFailure=now;
        reconnectionAttempts=0;
    }


    public Subscriber getSubscriber() {

        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {

        this.subscriber = subscriber;
    }

    public long getTimeOfInitialFailure() {

        return timeOfInitialFailure;
    }

    public void setTimeOfInitialFailure(long timeOfInitialFailure) {

        this.timeOfInitialFailure = timeOfInitialFailure;
    }

    public long getTimeOfLastFailure() {

        return timeOfLastFailure;
    }

    public void setTimeOfLastFailure(long timeOfLastFailure) {

        this.timeOfLastFailure = timeOfLastFailure;
    }

    public int getReconnectionAttempts() {

        return reconnectionAttempts;
    }

    private void incrementReconnectionAttempt(){
        reconnectionAttempts = reconnectionAttempts+1;
    }

    public boolean reconnect(){

        try {
            subscriber.initiateSubscription();

            subscriber.startSubscription();
            return true;
        } catch (LSClientException e) {
            timeOfLastFailure = (new Date()).getTime();
            incrementReconnectionAttempt();
            return false;
        }
    }

}
