package net.es.lookup.pubsub.client;

import net.es.lookup.client.SimpleLS;
import net.es.lookup.client.Subscriber;
import net.es.lookup.client.SubscriberListener;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;

import java.util.Iterator;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 6:31 PM
 */
public class ArchiveService implements SubscriberListener {

    private String sourceHost;
    private int sourcePort;
    private SimpleLS server;
    private Subscriber subscriber;
    private ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();

    public ArchiveService(String sourceHost, int sourcePort) throws LSClientException {
        System.out.println("Host:"+sourceHost+"; port:"+sourcePort);
        this.sourceHost = sourceHost;
        this.sourcePort = sourcePort;
        init();
    }

    private void init() throws LSClientException {
        server = new SimpleLS(sourceHost,sourcePort);
        server.connect();

    }

    public void start() throws LSClientException {
        Query q = new Query();
        subscriber = new Subscriber(server, q);
        subscriber.addListener(this);
        subscriber.startSubscription();
    }

    public void stop() throws LSClientException {
        System.out.println("Stop service");
        subscriber.removeListener(this);
        subscriber.stopSubscription();
    }

    public void onRecord(Record record) {

        if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_REGISTER)) {
            Message message = new Message(record.getMap());
            Map<String, Object> keyValues = record.getMap();
            Message operators = new Message();
            Message query = new Message();

            Iterator it = keyValues.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry<String, Object> pairs = (Map.Entry) it.next();
                operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
                query.add(pairs.getKey(), pairs.getValue());

            }

            try {
                db.queryAndPublishService(message, query, operators);
            } catch (DatabaseException e) {
                e.printStackTrace();
            } catch (DuplicateEntryException e) {
                e.printStackTrace();
            }

        } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_RENEW)) {
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            System.out.println("Got a renew message. Inserting into db");
            try {
                db.updateService(recordUri, message);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_EXPIRE)) {
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            System.out.println("Got an expired message. Inserting into db");
            try {
                db.updateService(recordUri, message);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_DELETE)) {
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            System.out.println("Got delete message. Inserting into db");
            try {
                db.updateService(recordUri, message);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
