package net.es.lookup.pubsub;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * This class keeps track of service to AMQueueManager and AMQueuePump mapping
 * Author: sowmya
 * Date: 10/29/13
 * Time: 4:43 PM
 */
public class QueueServiceMapping {

    private static HashMap<String, QueueManager> queueManagerHashMap = new HashMap<String, QueueManager>();
    private static HashMap<String,QueueDataGenerator> queueDGHashMap = new HashMap<String, QueueDataGenerator>();

    public static void addQueueManager(String service, QueueManager queueManager){
        queueManagerHashMap.put(service, queueManager);
    }

    public static void addQueueDataGenerator(String service, QueueDataGenerator queueDataGenerator){
        queueDGHashMap.put(service, queueDataGenerator);
    }

    public static QueueDataGenerator getQueueDataGenerator(String service){
        return queueDGHashMap.get(service);
    }

    public static QueueManager getQueueManager(String service){
        return queueManagerHashMap.get(service);
    }

    public static List<QueueDataGenerator> getAllQueueDataGenerator(){
        List result = new LinkedList();
        result.addAll(queueDGHashMap.values());
        return result;
    }


}
