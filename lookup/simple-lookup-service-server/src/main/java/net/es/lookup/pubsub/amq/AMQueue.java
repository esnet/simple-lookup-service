package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.pubsub.Queue;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.List;

/**
 * User: sowmya
 * Date: 2/25/13
 * Time: 3:23 PM
 */
public class AMQueue extends Queue {

    private String activemqUrl;
    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;
    private String qid = "";
    private static Logger LOG = Logger.getLogger(AMQueue.class);
    private final int BATCH_SIZE=500;
    private final long DELAY=200;
    ConnectionFactory activemqFactory;

    long messageTtl;
    boolean isPersistent;

    /**
     * The constructor
     * 1) creates a queue - starts a connection, creates the producer
     * 2) Generates queueId which used as topic. Subscriber uses this qid to subscribe to queues
     */

    public AMQueue(String qid) throws PubSubQueueException {

        //TODO: Make ActiveMQ user, password options configurable
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        QueueServiceConfigReader configReader = QueueServiceConfigReader.getInstance();

        messageTtl = configReader.getTtl();
        isPersistent = configReader.isQueuePersistent();
        this.qid = qid;
        activemqUrl = configReader.getUrl();
        activemqFactory = new ActiveMQConnectionFactory(user, password, activemqUrl);
        LOG.info("net.es.lookup.pubsub.amq.AMQueue.AMQueue: Queue Creation Successful!");

    }

    /**
     * This method returns the queueid associated with the queue.
     *
     * @return String - returns the queueid as a string
     */
    public String getQid() {

        return qid;

    }

    /**
     * This method pushes messages to the Active MQ Queue. Messages are rate controlled using BATCH_SIZE
     *
     * @param messages The message to be pushed to queue
     */
    public synchronized void push(List<Message> messages) throws PubSubQueueException {
        try {

            connection = activemqFactory.createConnection();

            connection.start();
            LOG.debug("net.es.lookup.pubsub.amq.AMQueue.AMQueue: Created connection for queue ");

        } catch (JMSException e) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueue.AMQueue: Error creating connection for Queue. "+ e.getMessage());
            LOG.error(e.getStackTrace());
            throw new PubSubQueueException(e.getMessage());
        }
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // false=NotTransacted
            topic = session.createTopic(qid);
            producer = session.createProducer(topic);
            producer.setTimeToLive(messageTtl);


            LOG.debug("net.es.lookup.pubsub.amq.AMQueue.AMQueue: Created ActiveMQ session, topic and producer for Queue");

            if (isPersistent) {
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else {
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }


        } catch (JMSException e) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueue.AMQueue: Error creating session/producer for Queue. "+ e.getMessage());
            throw new PubSubQueueException(e.getMessage());
        }

        if(messages.size()<=BATCH_SIZE){
            try {
                String strmsg = JSONMessage.toString(messages);
                send(strmsg);
            } catch (DataFormatException e) {
                LOG.error("net.es.lookup.pubsub.amq.AMQueue.push: Error pushing message to queue - DataFormatException mapped to PubSubQueueException" + e.getMessage());
                throw new PubSubQueueException(e.getMessage());
            }
        }else{
            int startIndex = 0;
            int endIndex=BATCH_SIZE;
            while(startIndex<messages.size()){
                List<Message> batchedMessages = messages.subList(startIndex, endIndex);
                try {
                    String strmsg = JSONMessage.toString(batchedMessages);
                    send(strmsg);
                    Thread.sleep(DELAY);
                } catch (DataFormatException e) {
                    LOG.error("net.es.lookup.pubsub.amq.AMQueue.push: Error pushing message to queue - DataFormatException mapped to PubSubQueueException" + e.getMessage());
                    throw new PubSubQueueException(e.getMessage());
                } catch (InterruptedException e) {
                    LOG.error("net.es.lookup.pubsub.amq.AMQueue.push: Error delaying between messages - InterruptedException mapped to PubSubQueueException" + e.getMessage());
                    throw new PubSubQueueException(e.getMessage());
                }

                startIndex += BATCH_SIZE;
                if(startIndex+BATCH_SIZE<messages.size()){
                    endIndex = startIndex+BATCH_SIZE;
                }else{
                    endIndex = messages.size();
                }
            }
        }

        close();
    }

    /**
     * This method sends a single message to the Active MQ Queue.
     *
     * @param message The message to be pushed to queue
     */
    private synchronized void send(String message) throws PubSubQueueException {
        try {

            TextMessage txtmsg = session.createTextMessage(message);
            LOG.debug("net.es.lookup.pubsub.amq.AMQueue.send: Received message to push - "+ message);
            producer.send(txtmsg);
            LOG.info("net.es.lookup.pubsub.amq.AMQueue.send: Pushed message to Queue - "+ txtmsg);
        } catch (JMSException e) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueue.send: Error pushing message to queue - JMSException mapped to PubSubQueueException "+ e.getMessage());
            throw new PubSubQueueException(e.getMessage());
        }
    }

    public void close() throws PubSubQueueException {

        try {
            if (session != null) {
                session.close();
            }
            if (connection != null) {
                connection.close();
            }

            LOG.info("net.es.lookup.pubsub.amq.AMQueue.close: Closed Queue ");

        } catch (JMSException e) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueue.close: Error closing Queue - JMSException mapped to PubSubQueueException "+ e.getMessage());
            throw new PubSubQueueException(e.getMessage());
        }
    }
}
