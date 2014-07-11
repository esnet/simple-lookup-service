package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.pubsub.Queue;
import net.es.lookup.utils.config.reader.LookupServiceConfigReader;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.util.List;
import java.util.UUID;

/**
 * User: sowmya
 * Date: 2/25/13
 * Time: 3:23 PM
 */
public class AMQueue extends Queue {

    private Connection connection;
    private Session session;
    private Topic topic;
    private MessageProducer producer;
    private String qid = "";
    private static Logger LOG = Logger.getLogger(AMQueue.class);

    /**
     * The constructor
     * 1) creates a queue - starts a connection, creates the producer
     * 2) Generates queueId which used as topic. Subscriber uses this qid to subscribe to queues
     */
    public AMQueue() throws PubSubQueueException {

        //TODO: Make ActiveMQ user, password options configurable
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        LookupServiceConfigReader lookupServiceConfigReader = LookupServiceConfigReader.getInstance();
        QueueServiceConfigReader configReader = QueueServiceConfigReader.getInstance();

        //String host = lookupServiceConfigReader.getHost();
        String host = configReader.getHost();
        int queueport = configReader.getPort();
        String protocol = configReader.getProtocol();
        long ttl = configReader.getTtl();
        boolean isPersistent = configReader.isQueuePersistent();

        String url = configReader.getUrl();
        ConnectionFactory factory = new ActiveMQConnectionFactory(user, password, url);

        try {

            connection = factory.createConnection();

            connection.start();
            LOG.debug("net.es.lookup.pubsub.amq.AMQueue.AMQueue: Created connection for queue ");

        } catch (JMSException e) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueue.AMQueue: Error creating connection for Queue. "+ e.getMessage());
            LOG.error(e.getStackTrace());
	    throw new PubSubQueueException(e.getMessage());
        }
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // false=NotTransacted
            qid = UUID.randomUUID().toString();
            topic = session.createTopic(qid);
            producer = session.createProducer(topic);
            producer.setTimeToLive(ttl);

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
     * This method pushes a single message to the Active MQ Queue.
     *
     * @param messages The message to be pushed to queue
     */
    public void push(List<Message> messages) throws PubSubQueueException {

        try {
            String strmsg = JSONMessage.toString(messages);
            TextMessage txtmsg = session.createTextMessage(strmsg);
            LOG.debug("net.es.lookup.pubsub.amq.AMQueue.push: Received message to push - "+ strmsg);
            producer.send(txtmsg);
            LOG.info("net.es.lookup.pubsub.amq.AMQueue.push: Pushed message to Queue - "+ txtmsg);
        } catch (DataFormatException e) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueue.push: Error pushing message to queue - DataFormatException mapped to PubSubQueueException"+ e.getMessage());
            throw new PubSubQueueException(e.getMessage());
        } catch (JMSException e) {
            LOG.error("net.es.lookup.pubsub.amq.AMQueue.push: Error pushing message to queue - JMSException mapped to PubSubQueueException "+ e.getMessage());
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
