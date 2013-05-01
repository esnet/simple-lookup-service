package net.es.lookup.pubsub.client;

import net.es.lookup.client.SimpleLS;
import net.es.lookup.client.Subscriber;
import net.es.lookup.client.SubscriberListener;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.internal.ConfigurationException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;
import net.es.lookup.utils.LookupServiceConfigReader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 4/25/13
 * Time: 6:01 PM
 */
public class ReplicationService implements SubscriberListener {
    private List<SimpleLS> servers;
    private List<Subscriber> subscribers;
    private ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();

    public ReplicationService() throws LSClientException {
        servers = new LinkedList<SimpleLS>();
        subscribers = new LinkedList<Subscriber>();
        LookupServiceConfigReader lcfg = LookupServiceConfigReader.getInstance();
        int count = lcfg.getSourceCount();

        for(int i=0; i<count;i++){
            try {
                String host = lcfg.getSourceHost(i);
                int port = lcfg.getSourcePort(i);
                SimpleLS server = new SimpleLS(host,port);
                servers.add(server);
            } catch (ConfigurationException e) {
                e.printStackTrace();
            }

        }
        init();
    }

    private void init() throws LSClientException {
        for (SimpleLS server: servers){
            server.connect();
        }


    }

    public void start() throws LSClientException {
        Query q = new Query();
        for (SimpleLS server: servers){
            Subscriber subscriber = new Subscriber(server, q);
            subscriber.addListener(this);
            subscriber.startSubscription();
            subscribers.add(subscriber);
        }


    }

    public void stop() throws LSClientException {
        System.out.println("Stop service");
        for (Subscriber subscriber: subscribers){
            subscriber.removeListener(this);
            subscriber.stopSubscription();
        }

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

        }else if(record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_RENEW)){
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            System.out.println("Got a renew message. Inserting into db");
            try {
                db.updateService(recordUri, message);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }else if(record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_EXPIRE)){
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            System.out.println("Got an expired message. Inserting into db");
            try {
                db.updateService(recordUri, message);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }else if(record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_DELETE)){
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            System.out.println("Got delete message. Inserting into db");
            try {
                db.deleteService(recordUri);
            } catch (DatabaseException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

}
