package net.es.lookup.publish;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DoesNotExistException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Author: sowmya
 * Date: 3/4/16
 * Time: 11:56 AM
 *
 * A publisher class that manages all queues and queries.
 * This class is singleton. Each sLS instance should have only one publisher instance
 */

public class Publisher {

    public static final int DEFAULT_MAX_PUSHEVENTS = 100;
    private static final long DEFAULT_PUSH_INTERVAL =120000;
    private HashMap<String,Queue> queryToQueueMap;

    private static Publisher instance;

    private int currentPushEvents;

    private long maxPushInterval;
    private Date lastPushed;
    private int maxPushEvents;
    private long pollInterval;

    public long getPollInterval() {

        return pollInterval;
    }

    public void setPollInterval(long pollInterval) {

        this.pollInterval = pollInterval;
    }

    public Date getLastPushed() {

        return lastPushed;
    }

    public void setLastPushed(Date lastPushed) {

        this.lastPushed = lastPushed;
    }

    public long getMaxPushInterval() {

        return maxPushInterval;
    }

    public void setMaxPushInterval(long maxPushInterval) {

        this.maxPushInterval = maxPushInterval;
    }

    public int getCurrentPushEvents() {

        return currentPushEvents;
    }

    public void setCurrentPushEvents(int currentPushEvents) {

        this.currentPushEvents = currentPushEvents;
    }

    public void incrementCurrentPushEvents() {

        this.currentPushEvents++;
    }

    public int getMaxPushEvents() {

        return maxPushEvents;
    }

    public void setMaxPushEvents(int maxPushEvents) {

        this.maxPushEvents = maxPushEvents;
    }

    private Publisher(){

        queryToQueueMap = new HashMap<String, Queue>();
        this.currentPushEvents = 0;
        this.maxPushEvents = DEFAULT_MAX_PUSHEVENTS;
        lastPushed = new Date();
        maxPushInterval = DEFAULT_PUSH_INTERVAL;

    }

    public static Publisher getInstance(){
        if(instance == null){
            createInstance();
        }
        return instance;
    }

    private static synchronized void createInstance(){
        if (instance == null){
            instance = new Publisher();
        }
    }

    public void addQueue(String query, Queue queue) throws DuplicateEntryException {

        if(!queryToQueueMap.containsKey(query)){
            queryToQueueMap.put(query,queue);
        }else{
            throw new DuplicateEntryException("Attempt to create duplicate entry");
        }

    }

    public void deleteQueue (String query, Queue queue) throws DoesNotExistException {
        if(queryToQueueMap.containsKey(query)){
           Queue qtoDelete =  queryToQueueMap.get(query);
           queryToQueueMap.remove(qtoDelete);
        }else{
            throw new DoesNotExistException("Attempting to delete nonexistent queue");
        }
    }

    public boolean containsQueue(String query){
        return queryToQueueMap.containsKey(query);
    }

    public Queue retrieveQueue(String query) {

        if(queryToQueueMap.containsKey(query)){
            return queryToQueueMap.get(query);
        }

        return null;
    }

    public Collection<Queue> getAllQueues(){
        if (!queryToQueueMap.isEmpty()){
            return queryToQueueMap.values();
        }
        return null;
    }

    public void eventNotification(Message record){

        //NOTE: currently assumes event is for all the queues. This will need to be modified if queries are implemented for queues.

        if(record != null){
            incrementCurrentPushEvents();

        }

    }
}
