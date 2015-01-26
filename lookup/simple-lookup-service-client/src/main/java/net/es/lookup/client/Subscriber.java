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
    private Subscriber instance = null;

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

       SubscribeRecord record = heartbeat();
                String queUrl = record.getLocator().get(0);
                this.queueUrl = queUrl;
                this.queue = record.getQueues().get(0);

        LOG.info("net.es.lookup.client.Subscriber: Initialized Subscriber");


    }

    /*
* This method contacts the subscribe URL and gets the queue URL and queue name.
* */
    public SubscribeRecord heartbeat() throws LSClientException {

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

            LOG.debug("net.es.lookup.client.Subscriber.heartbeat: Setting server config");
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
                LOG.info("Sent Heartbeat message");
                return record;
            } else {
                LOG.debug("net.es.lookup.client.Subscriber.heartbeat: Error in response:" + server.getErrorMessage());
                throw new LSClientException("Heartbeat Error in response. Response code: " + server.getResponseCode() + ". Error Message: " + server.getErrorMessage());
            }

        } else {
            LOG.debug("net.es.lookup.client.Subscriber.heartbeat: Error initializing server");
            throw new LSClientException("Subscriber heartbeat: Server Initialization Error");
        }

    }

    public void startSubscription() throws LSClientException {

        LOG.info("net.es.lookup.client.Subscriber: Starting Subscriber Connection");
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
                System.out.println(textMessage);
                List<Record> r = JSONParser.toRecords(textMessage.getText());
                recordNotifier(r);
            } catch (JMSException e) {
                LOG.error("net.es.lookup.client.Subscriber: Error in connection" + e.getMessage());
                List<Record> records = new ArrayList<Record>();
                ErrorRecord errorRecord = new ErrorRecord();
                try {
                    errorRecord.setErrorMessage(e.getMessage());
                } catch (RecordException e1) {
                    LOG.error("net.es.lookup.client.Subscriber: Unable to create error Record");
                }
                LOG.error("net.es.lookup.client.Subscriber: Subscriber instance" + instance);
                LOG.error("net.es.lookup.client.Subscriber: type" + errorRecord.getRecordType());
                errorRecord.add(ReservedKeys.SUBSCRIBER, instance.getSubscribeRequestUrl());
                errorRecord.add(ReservedKeys.QUEUE_URL, instance.getQueueUrl());
                errorRecord.add(ReservedKeys.QUEUE, instance.getQueue());
                records.add(errorRecord);
                recordNotifier(records);
                break;
            } catch (ParserException e) {
                LOG.error("net.es.lookup.client.Subscriber: Parser error" + e.getMessage());
            } catch (NullPointerException e) {
                LOG.warn("net.es.lookup.client.Subscriber: Null message" + e.getMessage());
            }
        }

    }

    private synchronized void recordNotifier(List<Record> records) {

        ListIterator<WeakReference<SubscriberListener>> listIterator = listeners.listIterator();
        while (listIterator.hasNext()) {

            LOG.info("net.es.lookup.client.Subscriber: Notifying listener");
            //potential deadlock -  need to use weak reference
            LOG.debug("Received records" + records.size());
            SubscriberListener listener = listIterator.next().get();
            for (Record record : records) {
                try {
                    Record tmp = record.duplicate();

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

    }

    /*
    * This method stops subscription temporarily
    * */
    public void stopSubscription() throws LSClientException {

        LOG.info("net.es.lookup.client.Subscriber: Stopping connection");
        if (conn != null) {
            try {
                conn.close();
                recordWaitThread = null;
                //listeners = null;


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
