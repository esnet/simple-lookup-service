package net.es.lookup.pubsub;

import java.util.HashMap;

/**
 *
 * This class keeps track of service to AMQueueManager and AMQueuePump mapping
 * Author: sowmya
 * Date: 10/29/13
 * Time: 4:43 PM
 */
public class QueueServiceMapping {

    private static HashMap<String, QueueManager> queueManagerHashMap = new HashMap<String, QueueManager>();
    private static HashMap<String,QueuePump> queuePumpHashMap = new HashMap<String, QueuePump>();

    public static void addQueueManager(String service, QueueManager queueManager){
        queueManagerHashMap.put(service, queueManager);
    }

    public static void addQueuePump(String service, QueuePump queuePump){
        queuePumpHashMap.put(service, queuePump);
    }

    public static QueuePump getQueuePump(String service){
        return queuePumpHashMap.get(service);
    }

    public static QueueManager getQueueManager(String service){
        return queueManagerHashMap.get(service);
    }


}
