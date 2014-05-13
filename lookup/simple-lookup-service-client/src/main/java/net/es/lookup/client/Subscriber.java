package net.es.lookup.client;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.queries.Query;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.records.ErrorRecord;
import net.es.lookup.records.PubSub.SubscribeRecord;
import net.es.lookup.records.Record;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.lang.ref.WeakReference;
import java.util.*;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 3:42 PM
 */
public class Subscriber {

    private SimpleLS server;
    private Query query;
    private String subscribeRequestUrl;

    private Connection conn;
    private String queueUrl;
    private String queue;
    private Session session;
    private Topic topic;
    private MessageConsumer consumer;

    private Thread recordWaitThread;
    private Subscriber instance=null;

    private List<WeakReference<SubscriberListener>> listeners;

    private static Logger LOG = Logger.getLogger(Subscriber.class);

    public Subscriber(SimpleLS server, Query query, String subscribeURL) throws LSClientException {

        this.server = server;
        this.server.connect();
        this.query = query;
        LOG.info("net.es.lookup.client.Subscriber: Creating Subscriber");
        if (subscribeURL != null && !subscribeURL.isEmpty()) {
            this.subscribeRequestUrl = subscribeURL;
        } else {
            throw new LSClientException("subscribe URL is not specified");
        }
        listeners = new LinkedList<WeakReference<SubscriberListener>>();
        LOG.info("net.es.lookup.client.Subscriber: Created Subscriber listener");
        initiateSubscription();
        LOG.info("net.es.lookup.client.Subscriber: Created Subscriber");
        this.instance = this;
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

    /*
    * This method contacts the subscribe URL and gets the queue URL and queue name.
    * */
    public void initiateSubscription() throws LSClientException {

        if (server != null && server.getStatus().equals(ReservedValues.SERVER_STATUS_ALIVE)) {
            LOG.info("net.es.lookup.client.Subscriber: Initiating subscription");
            LOG.debug("net.es.lookup.client.Subscriber: Parsing query");
            String queryString = "";
            if (query != null) {

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
                this.queueUrl = queUrl+ "?jms.prefetchPolicy.all=1";
                this.queue = record.getQueues().get(0);
            } else {
                LOG.debug("net.es.lookup.client.Subscriber: Error in response:" + server.getErrorMessage());
                throw new LSClientException("Error in response. Response code: " + server.getResponseCode() + ". Error Message: " + server.getErrorMessage());
            }

        } else {
            LOG.debug("net.es.lookup.client.Subscriber: Error initializing server");
            throw new LSClientException("Server Initialization Error");
        }

        LOG.info("net.es.lookup.client.Subscriber: Initialized Subscriber");


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
                ErrorRecord errorRecord = new ErrorRecord();
                try {
                    errorRecord.setErrorMessage(e.getMessage());
                } catch (RecordException e1) {
                    LOG.error("net.es.lookup.client.Subscriber: Unable to create error Record");
                }
                LOG.error("net.es.lookup.client.Subscriber: Subscriber instance"+instance);
                LOG.error("net.es.lookup.client.Subscriber: type"+ errorRecord.getRecordType());
                errorRecord.add(ReservedKeys.SUBSCRIBER, instance.getSubscribeRequestUrl());
                errorRecord.add(ReservedKeys.QUEUE_URL, instance.getQueueUrl());
                errorRecord.add(ReservedKeys.QUEUE, instance.getQueue());
                recordNotifier(errorRecord);
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
                LOG.info("net.es.lookup.client.Subscriber: Notifying listener");
                //potential deadlock -  need to use weak reference
                LOG.debug(record.getMap().keySet().toString());
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

    /*
    * This method stops subscription temporarily
    * */
    public void stopSubscription() throws LSClientException {

        LOG.info("net.es.lookup.client.Subscriber: Stopping connection");
        if (conn != null) {
            try {

                recordWaitThread = null;
                //listeners = null;
                consumer.close();
                conn.close();
            } catch (JMSException e) {
                LOG.error("net.es.lookup.client.Subscriber: Connection Exception = " + e.getMessage());
                throw new LSClientException(e.getMessage());
            }
        }

    }

    /*
    * This is for permanent shutdown of subscriber
    * */
    public void shutdown() throws LSClientException {
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
