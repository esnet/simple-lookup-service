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

    public Subscriber(SimpleLS server, Query query) throws LSClientException {

        this(server, query, "");
    }

    public Subscriber(SimpleLS server, Query query, String subscribeURL) throws LSClientException {

        this.server = server;
        this.server.connect();
        this.query = query;

        if (subscribeURL != null && !subscribeURL.isEmpty()) {
            this.subscribeRequestUrl = subscribeURL;
        } else {
            this.subscribeRequestUrl = DEFAULT_SUBSCRIBE_REQUEST_URL;
        }
        listeners = new LinkedList<WeakReference<SubscriberListener>>();
        initiateSubscription();
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
            String queryString = "";
            if (query != null) {
                try {
                    queryString = JSONParser.toString(query);
                } catch (ParserException e) {
                    throw new LSClientException(e.getMessage());
                }
            }

            System.out.println(queryString);


            server.setRelativeUrl(subscribeRequestUrl);
            server.setConnectionType("POST");
            server.setData(queryString);
            server.send();
            if (server.getResponseCode() == 200) {

                String response = server.getResponse();
                SubscribeRecord record = null;
                try {
                    record = (SubscribeRecord) JSONParser.toRecord(response);
                } catch (ParserException e) {
                    throw new LSClientException(e.getMessage());
                }
                String queUrl = record.getLocator().get(0);
                    this.queueUrl = queUrl;
                    this.queue = record.getQueues().get(0);
            } else {
                throw new LSClientException("Error in response. Response code: " + server.getResponseCode() + ". Error Message: " + server.getErrorMessage());
            }

        } else {
            throw new LSClientException("Server Initialization Error");
        }


    }

    public void startSubscription() throws LSClientException {

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

    }

    public Record retrieveMessage() throws LSClientException {

        Record record;
        try {

            TextMessage textMessage = (TextMessage) consumer.receive();

            record = JSONParser.toRecord(textMessage.getText());


        } catch (JMSException e) {
            throw new LSClientException(e.getMessage());
        } catch (ParserException e) {
            throw new LSClientException(e.getMessage());
        }
        return record;
    }


    public synchronized SubscriberListener addListener(SubscriberListener listener) {
        if (listener != null) {
            WeakReference<SubscriberListener> weakListener = new WeakReference<SubscriberListener>(listener);

            listeners.add(weakListener);
            if (recordWaitThread == null && conn != null) {
                createRecordWaitThread();
            }

        }

        return listener;

    }

    public synchronized SubscriberListener removeListener(SubscriberListener listener) {
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
        if (result) {
            return listener;
        } else {
            return null;
        }


    }

    private void createRecordWaitThread() {

        System.out.println("Creating thread");
        recordWaitThread = new Thread() {
            public void run() {

                waitForRecords();
            }
        };
        recordWaitThread.setName("+"+Math.random());
        System.out.println("Created thread. About to start thread: "+ recordWaitThread.getName());
        recordWaitThread.start();
        System.out.println("Thread started");
    }


    private void waitForRecords() {

        while (true) {
            try {
                System.out.println("Waiting for records");
                TextMessage textMessage = (TextMessage) consumer.receive();

                Record r = JSONParser.toRecord(textMessage.getText());
                recordNotifier(r);
            } catch (JMSException e) {
                e.printStackTrace();
                break;
            } catch (ParserException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }

    private synchronized void recordNotifier(Record record) {

        ListIterator<WeakReference<SubscriberListener>> listIterator = listeners.listIterator();
        while (listIterator.hasNext()) {
            try {
                System.out.println("Notifying listener");

                //add record.duplicate() for cloning
                //potential deadlock -  need to use weak reference
                Record tmp = record.duplicate();
                SubscriberListener listener = listIterator.next().get();
                if (listener != null) {

                    try {
                        listener.onRecord(tmp);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    listIterator.remove();
                }

            } catch (RecordException e) {
                e.printStackTrace();
            }
        }

    }

    public void stopSubscription() throws LSClientException {

        if (conn != null) {
            try {
                System.out.println("Stopping connection");
                recordWaitThread = null;
                listeners = null;
                consumer.close();
                conn.close();
            } catch (JMSException e) {
                throw new LSClientException(e.getMessage());
            }
        }

    }
}
