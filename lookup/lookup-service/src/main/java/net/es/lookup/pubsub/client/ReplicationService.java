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
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Author: sowmya
 * Date: 4/25/13
 * Time: 6:01 PM
 */
public class ReplicationService implements SubscriberListener {

    private List<SimpleLS> servers;
    private List<List<Map<String, Object>>> queries;
    private List<Subscriber> subscribers;
    private ServiceDAOMongoDb db = ServiceDAOMongoDb.getInstance();
    SubscriberConfigReader subscriberConfigReadercfg;
    private static Logger LOG = Logger.getLogger(ReplicationService.class);

    public ReplicationService() throws LSClientException {

        subscriberConfigReadercfg = SubscriberConfigReader.getInstance();
        servers = new ArrayList<SimpleLS>();
        queries = new ArrayList<List<Map<String, Object>>>();
        subscribers = new ArrayList<Subscriber>();
        int count = subscriberConfigReadercfg.getSourceCount();
        LOG.info("net.es.lookup.pubsub.client.ReplicationService: Initializing "+count+ " hosts");
        for (int i = 0; i < count; i++) {
            try {
                String host = subscriberConfigReadercfg.getSourceHost(i);
                int port = subscriberConfigReadercfg.getSourcePort(i);
                List<Map<String, Object>> serverQueries = subscriberConfigReadercfg.getQueries(i);
                queries.add(i, serverQueries);
                SimpleLS server = new SimpleLS(host, port);
                servers.add(server);

            } catch (ConfigurationException e) {
                LOG.error("net.es.lookup.pubsub.client.ReplicationService: Initializing "+count+ " hosts");
                throw new LSClientException("net.es.lookup.pubsub.client.ReplicationService: Error initializing subscribe hosts -"+ e.getMessage());
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
        LOG.info("net.es.lookup.pubsub.client.ReplicationService.start: Creating and starting the subscriber connections");
        int index = 0;
        for (SimpleLS server : servers) {
            List<Map<String, Object>> queryList = null;

            queryList = queries.get(index);
            index++;
            if (queryList != null && !queryList.isEmpty()) {
                for (int i=0; i<queryList.size();i++) {
                    Map<String,Object> m = queryList.get(i);
                    if (m != null) {
                        try {
                            Query query = new Query(m);
                            Subscriber subscriber = new Subscriber(server, query);
                            subscriber.addListener(this);
                            subscriber.startSubscription();
                            subscribers.add(subscriber);
                        } catch (RecordException e) {
                            LOG.error("net.es.lookup.pubsub.client.ReplicationService.start: Error defining query");
                            throw new LSClientException("Query could not be defined"+ e.getMessage());
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

        LOG.info("net.es.lookup.pubsub.client.ReplicationService.start: Created and initialized "+ subscribers.size() +" subscriber connections");

    }

    public void stop() throws LSClientException {

        LOG.info("net.es.lookup.pubsub.client.ReplicationService.stop: Stopping "+ subscribers.size() +" subscriber connections");
        for (Subscriber subscriber : subscribers) {
            subscriber.removeListener(this);
            subscriber.stopSubscription();
        }
        LOG.info("net.es.lookup.pubsub.client.ReplicationService.stop: Stopped "+ subscribers.size() +" subscriber connections");

    }


    public void onRecord(Record record) {
        LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: Processing Received message");
        if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_REGISTER)) {
            LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: insert as new record");
            Message message = new Message(record.getMap());
            Map<String, Object> keyValues = record.getMap();
            Message operators = new Message();
            Message query = new Message();

            Iterator it = keyValues.entrySet().iterator();
            LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: Constructing query based on message");
            while (it.hasNext()) {

                Map.Entry<String, Object> pairs = (Map.Entry) it.next();
                operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
                query.add(pairs.getKey(), pairs.getValue());

            }
            LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: Check and insert record");
            try {
                db.queryAndPublishService(message, query, operators);
            } catch (DatabaseException e) {
                LOG.error("net.es.lookup.pubsub.client.ReplicationService.onRecord: Error inserting record. Database Error"+ e.getMessage());
            } catch (DuplicateEntryException e) {
                LOG.error("net.es.lookup.pubsub.client.ReplicationService.onRecord: Error inserting record. Duplicate Exception Error"+ e.getMessage());
            }

            LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: Inserted record");

        }else if(record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_RENEW)){
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: renew existing record");
            try {
                db.updateService(recordUri, message);
            } catch (DatabaseException e) {
                LOG.error("net.es.lookup.pubsub.client.ReplicationService.onRecord: Error renewing record. Database Error"+ e.getMessage());
            }
        }else if(record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_EXPIRE)){
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: received expired record");
            try {
                db.updateService(recordUri, message);
            } catch (DatabaseException e) {
                LOG.error("net.es.lookup.pubsub.client.ReplicationService.onRecord: Error expiring record. Database Error"+ e.getMessage());
            }
        }else if(record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_DELETE)){
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            LOG.info("net.es.lookup.pubsub.client.ReplicationService.onRecord: received deleted record");
            try {
                db.deleteService(recordUri);
            } catch (DatabaseException e) {
                LOG.error("net.es.lookup.pubsub.client.ReplicationService.onRecord: Error deleting record. Database Error"+ e.getMessage());
            }
        }
    }

}
