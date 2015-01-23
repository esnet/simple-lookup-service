package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.internal.PubSubQueryException;
import net.es.lookup.pubsub.QueueDataGenerator;
import net.es.lookup.pubsub.QueueServiceMapping;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import org.apache.log4j.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Author: sowmya
 * Date: 3/19/13
 * Time: 1:44 PM
 *
 * This class manages the filling up of queues. The APIs fill up a temporary queue called recordQueue.
 * Every 'x' minutes, the Quartz scheduler triggers the AMQueuePump to psh data out to the exchange.
 */


public class AMQueueDataGenerator implements QueueDataGenerator {

    private static AMQueueDataGenerator instance = null;
    private String serviceName;
    private static Logger LOG = Logger.getLogger(AMQueueDataGenerator.class);
    private Queue<Message> recordQueue;

    private int BATCH_SIZE;
    QueueServiceConfigReader configReader;


    public AMQueueDataGenerator(String serviceName) {

        this.serviceName = serviceName;
        QueueServiceMapping.addQueueDataGenerator(serviceName, this);
        recordQueue = new ConcurrentLinkedQueue<Message>();
        configReader = QueueServiceConfigReader.getInstance();
        BATCH_SIZE = configReader.getBatchSize();
    }


    public static synchronized void setInstance(AMQueueDataGenerator amQueueDataGenerator) {

        if (instance != null) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueuePump.setInstance: Attempting to create second AMQueuePump. So throwing RuntimeException");
            throw new RuntimeException("net.es.lookup.pubsub.amq.AMQueuePump.setInstance: Attempt to create second instance");
        } else {
            instance = amQueueDataGenerator;
        }
    }


    public void fillQueues(List<Message> messageList) throws PubSubQueryException {

        LOG.info("net.es.lookup.pubsub.amq.AMQueuePump.fillQueues: Filling up queues with message");
        recordQueue.addAll(messageList);

    }

    public void pushToQueue() {

        LOG.info("net.es.lookup.pubsub.amq.AMQueuePump.executePush: Push messages to exchange");
        AMQueueManager amQueueManager = (AMQueueManager) QueueServiceMapping.getQueueManager(serviceName);
        List<Message> queries = amQueueManager.getAllQueries();

        if (!queries.isEmpty()) {
            //optimized for queries with only 1 key-value pair
            for (Message query : queries) {
                Map<String, Object> queryMap = query.getMap();
                List<Message> messagesToSend = new LinkedList<Message>();
                LOG.debug("net.es.lookup.pubsub.amq.AMQueuePump.executePush: Query" + query.getMap());
                int i = 0;
                while (i < recordQueue.size()) {

                    Message message = recordQueue.remove();

                    if (i >= BATCH_SIZE) break;

                    if (queryMap.size() == 1 && queryMap.containsKey(ReservedKeys.RECORD_OPERATOR_SUFFIX)) {
                        messagesToSend.add(message);


                    } else {
                        for (String key : queryMap.keySet()) {
                            if (message.hasKey(key) && message.getKey(key).equals(queryMap.get(key))) {
                                LOG.debug("net.es.lookup.pubsub.amq.AMQueuePump.executePush: Message " + message.getMap() + " mapped to query" + query.getMap());
                                messagesToSend.add(message);

                            }
                        }
                    }
                    i++;
                }

                if (!messagesToSend.isEmpty()) {
                    List<String> qids = null;
                    try {
                        qids = amQueueManager.getQueues(query);
                    } catch (PubSubQueryException e) {
                        LOG.error("net.es.lookup.pubsub.amq.AMQueuePump.executePush" + e.getMessage());
                    }

                    for (String qid : qids) {
                        if (qid != null && !qid.isEmpty()) {
                            amQueueManager.push(qid, messagesToSend);
                        }
                    }
                }
            }
        }
    }
}
