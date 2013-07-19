package net.es.lookup.client;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.queries.Query;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.records.PubSub.SubscribeRecord;
import net.es.lookup.records.Record;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import sun.font.CreatedFontTracker;

import javax.jms.*;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 3:42 PM
 */
public class Subscriber {

    private SimpleLS server;
    private Query query;
    private String DEFAULT_SUBSCRIBE_REQUEST_URL = "/lookup/subscribe";
    private String subscribeRequestUrl = DEFAULT_SUBSCRIBE_REQUEST_URL;

    private Connection conn;
    private String queueUrl;
    private String queue;
    private Session session;
    private Topic topic;
    private MessageConsumer consumer;

    private Thread recordWaitThread;

    private List<WeakReference<SubscriberListener>> listeners;

    private static Logger LOG = Logger.getLogger(Subscriber.class);

    public Subscriber(SimpleLS server, Query query) throws LSClientException {

        this(server, query, "");
    }

    public Subscriber(SimpleLS server, Query query, String subscribeURL) throws LSClientException {

        this.server = server;
        this.server.connect();
        this.query = query;
        LOG.info("net.es.lookup.client.Subsciber: Creating Subscriber");
        if (subscribeURL != null && !subscribeURL.isEmpty()) {
            this.subscribeRequestUrl = subscribeURL;
        } else {
            this.subscribeRequestUrl = DEFAULT_SUBSCRIBE_REQUEST_URL;
        }
        listeners = new LinkedList<WeakReference<SubscriberListener>>();
        LOG.info("net.es.lookup.client.Subsciber: Created Subscriber listener");
        initiateSubscription();
        LOG.info("net.es.lookup.client.Subsciber: Created Subscriber");
    }

    public SimpleLS getServer() {

        return server;
    }


    public Query getQuery() {

        return query;
    }


    public String getSubscribeRequestUrl() {

        return subscribeRequestUrl;
    }


    public String getQueueUrl() {

        return queueUrl;
    }

    public String getQueue() {

        return queue;
    }


    private void initiateSubscription() throws LSClientException {

        if (server != null && server.getStatus().equals(ReservedKeys.SERVER_STATUS_ALIVE)) {
            LOG.info("net.es.lookup.client.Subscriber: Initiating subscription");
            LOG.debug("net.es.lookup.client.Subscriber: Parsing query");
            String queryString = "";
            if (query != null) {
                LOG.debug("net.es.lookup.client.Subscriber: Query is not null");
                try {
                    queryString = JSONParser.toString(query);
                } catch (ParserException e) {
                    throw new LSClientException(e.getMessage());
                }
            }

            LOG.debug("net.es.lookup.client.Subscriber: Query=" + queryString);

            LOG.debug("net.es.lookup.client.Subscriber: Setting server config");
            server.setRelativeUrl(subscribeRequestUrl);
            server.setConnectionType("POST");
            server.setData(queryString);
            LOG.debug("net.es.lookup.client.Subscriber: Sending subscribe request to server");
            server.send();
            LOG.debug("net.es.lookup.client.Subscriber: Response Code from server=" + server.getResponseCode());
            if (server.getResponseCode() == 200) {
                LOG.debug("net.es.lookup.client.Subscriber: Parsing response");
                String response = server.getResponse();
                SubscribeRecord record = null;
                try {
                    record = (SubscribeRecord) JSONParser.toRecord(response);
                } catch (ParserException e) {
                    LOG.error("net.es.lookup.client.Subscriber: Error Parsing response");
                    throw new LSClientException(e.getMessage());
                }
                String queUrl = record.getLocator().get(0);
                this.queueUrl = queUrl;
                this.queue = record.getQueues().get(0);
            } else {
                LOG.debug("net.es.lookup.client.Subscriber: Error in response:" + server.getErrorMessage());
                throw new LSClientException("Error in response. Response code: " + server.getResponseCode() + ". Error Message: " + server.getErrorMessage());
            }

        } else {
            LOG.debug("net.es.lookup.client.Subscriber: Error initializing server");
            throw new LSClientException("Server Initialization Error");
        }

        LOG.info("net.es.lookup.client.Subsciber: Initialized Subscriber");


    }

    public void startSubscription() throws LSClientException {

        LOG.info("net.es.lookup.client.Subsciber: Starting Subscriber Connection");
        String user = ActiveMQConnection.DEFAULT_USER;
        String password = ActiveMQConnection.DEFAULT_PASSWORD;

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(user, password, queueUrl);

        try {
            conn = connectionFactory.createConnection();
            session = conn.createSession(false,
                    Session.AUTO_ACKNOWLEDGE);
            topic = session
                    .createTopic(queue);
            consumer = session.createConsumer(topic);
            conn.start();
        } catch (JMSException e) {
            throw new LSClientException("Error starting connection: " + e.getMessage());
        }

        if (!listeners.isEmpty() && recordWaitThread == null) {
            createRecordWaitThread();
        }
        LOG.info("net.es.lookup.client.Subscriber: Started Subscriber Connection");

    }

    public Record retrieveMessage() throws LSClientException {

        LOG.info("net.es.lookup.client.Subscriber: REtrieving message");
        Record record;
        try {

            TextMessage textMessage = (TextMessage) consumer.receive();

            record = JSONParser.toRecord(textMessage.getText());


        } catch (JMSException e) {
            LOG.error("net.es.lookup.client.Subscriber: Connection exception" + e.getMessage());
            throw new LSClientException(e.getMessage());
        } catch (ParserException e) {
            LOG.error("net.es.lookup.client.Subscriber: Connection exception" + e.getMessage());
            throw new LSClientException(e.getMessage());
        }
        return record;
    }


    public synchronized SubscriberListener addListener(SubscriberListener listener) {

        LOG.info("net.es.lookup.client.Subscriber: Adding listener");
        if (listener != null) {
            WeakReference<SubscriberListener> weakListener = new WeakReference<SubscriberListener>(listener);

            listeners.add(weakListener);
            if (recordWaitThread == null && conn != null) {
                createRecordWaitThread();
            }

        }
        LOG.info("net.es.lookup.client.Subscriber: Successfully Added listener");
        return listener;

    }

    public synchronized SubscriberListener removeListener(SubscriberListener listener) {

        LOG.info("net.es.lookup.client.Subscriber: Removing listener");
        ListIterator<WeakReference<SubscriberListener>> listIterator = listeners.listIterator();
        boolean result = false;
        while (listIterator.hasNext()) {
            SubscriberListener subscriberListener = listIterator.next().get();
            if (subscriberListener.equals(listener)) {
                listIterator.remove();
                result = true;
                break;
            }
        }
        if (listeners.isEmpty()) {
            recordWaitThread = null;
        }
        LOG.info("net.es.lookup.client.Subscriber: Successfully removed listener");
        if (result) {
            return listener;
        } else {
            return null;
        }


    }

    private void createRecordWaitThread() {

        LOG.info("net.es.lookup.client.Subscriber: Creating message wait thread");
        recordWaitThread = new Thread() {
            public void run() {

                waitForRecords();
            }
        };
        recordWaitThread.start();
        LOG.info("Message wait Thread started");
    }


    private void waitForRecords() {

        while (true) {
            try {
                LOG.debug("net.es.lookup.client.Subscriber: Waiting for records");
                TextMessage textMessage = (TextMessage) consumer.receive();

                Record r = JSONParser.toRecord(textMessage.getText());
                recordNotifier(r);
            } catch (JMSException e) {
                LOG.error("net.es.lookup.client.Subscriber: Error in connection" + e.getMessage());
                break;
            } catch (ParserException e) {
                LOG.error("net.es.lookup.client.Subscriber: Parser error" + e.getMessage());
            }
        }

    }

    private synchronized void recordNotifier(Record record) {

        ListIterator<WeakReference<SubscriberListener>> listIterator = listeners.listIterator();
        while (listIterator.hasNext()) {
            try {
                LOG.info("net.es.lookup.client.Subscriber: PNotifying listener");
                //potential deadlock -  need to use weak reference
                Record tmp = record.duplicate();
                SubscriberListener listener = listIterator.next().get();
                if (listener != null) {

                    try {
                        listener.onRecord(tmp);
                    } catch (Exception e) {
                        LOG.error("net.es.lookup.client.Subscriber: Exception = " + e.getMessage());
                    }
                } else {
                    listIterator.remove();
                }

            } catch (RecordException e) {
                LOG.error("net.es.lookup.client.Subscriber: Record Exception = " + e.getMessage());
            }
        }

    }

    public void stopSubscription() throws LSClientException {

        LOG.info("net.es.lookup.client.Subscriber: Stopping connection");
        if (conn != null) {
            try {

                recordWaitThread = null;
                listeners = null;
                consumer.close();
                conn.close();
            } catch (JMSException e) {
                LOG.error("net.es.lookup.client.Subscriber: Connection Exception = " + e.getMessage());
                throw new LSClientException(e.getMessage());
            }
        }

    }
}
