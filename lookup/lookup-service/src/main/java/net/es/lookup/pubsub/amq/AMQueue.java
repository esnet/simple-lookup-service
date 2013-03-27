package net.es.lookup.pubsub.amq;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.pubsub.Queue;
import net.es.lookup.utils.QueueServiceConfigReader;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


import javax.jms.*;
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


    /**
     * The constructor
     * 1) creates a queue - starts a connection, creates the producer
     * 2) Generates queueId which used as topic. Subscriber uses this qid to subscribe to queues
     */
    public AMQueue() throws QueueException {

        //TODO: Make ActiveMQ options configurable
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;
        QueueServiceConfigReader configReader = QueueServiceConfigReader.getInstance();

        String host = configReader.getHost();
        int port = configReader.getPort();
        String protocol = configReader.getProtocol();
        long ttl = configReader.getTtl();
        boolean isPersistent = configReader.isQueuePersistent();

        String url = protocol + "://" + host + ":" + port;

        ConnectionFactory factory = new ActiveMQConnectionFactory(user,password,url);
        try {

            connection = factory.createConnection();

            connection.start();

        } catch (JMSException e) {
            throw new QueueException(e.getMessage());
        }
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE); // false=NotTransacted
            qid = UUID.randomUUID().toString();
            topic = session.createTopic(qid);
            producer = session.createProducer(topic);
            producer.setTimeToLive(ttl);

            if (isPersistent) {
                producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            } else {
                producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            }


        } catch (JMSException e) {
            throw new QueueException(e.getMessage());
        }

    }

    public String getQid() {

        return qid;

    }

    public void push(Message message) throws QueueException {

        try {
            String strmessage = JSONMessage.toString(message);
            TextMessage msg = session.createTextMessage(strmessage);
            producer.send(msg);
        } catch (DataFormatException e) {
            throw new QueueException(e.getMessage());
        } catch (JMSException e) {
            throw new QueueException(e.getMessage());
        }
    }

    public void close() throws QueueException {

        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                throw new QueueException(e.getMessage());
            }
        }
    }
}
