package net.es.lookup.pubsub.client;

import net.es.lookup.client.SimpleLS;
import net.es.lookup.client.Subscriber;
import net.es.lookup.client.SubscriberListener;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.common.exception.internal.ConfigurationException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;
import net.es.lookup.utils.LookupServiceConfigReader;
import net.es.lookup.utils.SubscriberConfigReader;

import java.util.*;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 6:31 PM
 */
public class ArchiveService implements SubscriberListener {


    private List<SimpleLS> servers;
    private List<List<Map<String, Object>>> queries;
    private List<Subscriber> subscribers;
    private ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();
    SubscriberConfigReader subscriberConfigReadercfg;

    public ArchiveService() throws LSClientException {

        subscriberConfigReadercfg = SubscriberConfigReader.getInstance();
        servers = new ArrayList<SimpleLS>();
        queries = new ArrayList<List<Map<String, Object>>>();
        subscribers = new ArrayList<Subscriber>();
        int count = subscriberConfigReadercfg.getSourceCount();

        for (int i = 0; i < count; i++) {
            try {
                String host = subscriberConfigReadercfg.getSourceHost(i);
                int port = subscriberConfigReadercfg.getSourcePort(i);
                List<Map<String, Object>> serverQueries = subscriberConfigReadercfg.getQueries(i);
                queries.add(i, serverQueries);
                SimpleLS server = new SimpleLS(host, port);
                servers.add(server);

            } catch (ConfigurationException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        }
        init();
    }

    private void init() throws LSClientException {

        for (SimpleLS server : servers) {
            server.connect();
        }


    }

    public void start() throws LSClientException {

        int index = 0;
        for (SimpleLS server : servers) {
            List<Map<String, Object>> queryList = null;

            queryList = queries.get(index);
            index++;
            if (queryList != null && !queryList.isEmpty()) {
                for (int i=0; i<queryList.size();i++) {
                    System.out.println(i+" query size"+queryList.size());
                    System.out.println(i+" query"+queryList.get(i).values());
                    Map<String,Object> m = queryList.get(i);
                    if (m != null) {
                        try {
                            Query query = new Query(m);
                            Subscriber subscriber = new Subscriber(server, query);
                            subscriber.addListener(this);
                            System.out.println("I'm back!!");
                            subscriber.startSubscription();
                            System.out.println("I'm back!!");
                            subscribers.add(subscriber);
                            System.out.println("Finished an iteration");
                        } catch (RecordException e) {
                          e.printStackTrace();
                        }
                    }

                }
            } else {
                //if no list exists, create empty query
                Query query = new Query();
                Subscriber subscriber = new Subscriber(server, query);
                subscriber.addListener(this);
                subscriber.startSubscription();
                subscribers.add(subscriber);

            }

        }


    }

    public void stop() throws LSClientException {

        System.out.println("Stop service");
        for (Subscriber subscriber : subscribers) {
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
