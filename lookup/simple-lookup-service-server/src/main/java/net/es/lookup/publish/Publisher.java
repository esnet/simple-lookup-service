package net.es.lookup.publish;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DoesNotExistException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;

import java.util.Collection;
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

    private HashMap<String,Queue> queryToQueueMap;

    private static Publisher instance;

    private Publisher(){

        queryToQueueMap = new HashMap<String, Queue>();
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
            Collection<Queue> queues = getAllQueues();
            for(Queue queue:queues){
                queue.incrementCurrentPushEvents();
            }

        }

    }
}
