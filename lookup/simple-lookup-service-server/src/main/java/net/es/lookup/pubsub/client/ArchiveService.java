package net.es.lookup.pubsub.client;

import net.es.lookup.client.QueryClient;
import net.es.lookup.client.SimpleLS;
import net.es.lookup.client.Subscriber;
import net.es.lookup.client.SubscriberListener;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.common.exception.internal.ConfigurationException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.DBMapping;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;
import net.es.lookup.utils.LookupServiceConfigReader;
import net.es.lookup.utils.SubscriberConfigReader;
import org.apache.log4j.Logger;

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
    private ServiceDAOMongoDb db;
    SubscriberConfigReader subscriberConfigReadercfg;
    private static Logger LOG = Logger.getLogger(ArchiveService.class);

    public ArchiveService(String serviceName) throws LSClientException, ConfigurationException {

        db = DBMapping.getDb(serviceName);

        subscriberConfigReadercfg = SubscriberConfigReader.getInstance();
        servers = new ArrayList<SimpleLS>();
        queries = new ArrayList<List<Map<String, Object>>>();
        subscribers = new ArrayList<Subscriber>();
        int count = subscriberConfigReadercfg.getSourceCount();
        LOG.info("net.es.lookup.pubsub.client.ArchiveService: Initializing " + count + " hosts");
        for (int i = 0; i < count; i++) {
            try {
                String host = subscriberConfigReadercfg.getSourceHost(i);
                int port = subscriberConfigReadercfg.getSourcePort(i);
                List<Map<String, Object>> serverQueries = subscriberConfigReadercfg.getQueries(i);
                queries.add(i, serverQueries);
                SimpleLS server = new SimpleLS(host, port);
                servers.add(server);

            } catch (ConfigurationException e) {
                LOG.error("net.es.lookup.pubsub.client.ArchiveService: Initializing " + count + " hosts");
                throw new LSClientException("net.es.lookup.pubsub.client.ArchiveService: Error initializing subscribe hosts -" + e.getMessage());
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

        LOG.info("net.es.lookup.pubsub.client.ArchiveService.start: Creating and starting the subscriber connections");
        int index = 0;
        for (SimpleLS server : servers) {
            List<Map<String, Object>> queryList = null;

            queryList = queries.get(index);
            index++;


            if (queryList != null && !queryList.isEmpty()) {
                for (int i = 0; i < queryList.size(); i++) {
                    Map<String, Object> m = queryList.get(i);
                    if (m != null) {
                        try {
                            Query query = new Query(m);
                            Subscriber subscriber = new Subscriber(server, query);
                            subscriber.addListener(this);

                            //get the initial set of records before starting subscribe
                            try {
                                getRecords(query, server);
                            } catch (ParserException e) {
                                LOG.error("net.es.lookup.pubsub.client.ArchiveService.start: Error parsing query results - " + e.getMessage());
                            }
                            subscriber.startSubscription();
                            subscribers.add(subscriber);
                        } catch (QueryException e) {
                            LOG.error("net.es.lookup.pubsub.client.ArchiveService.start: Error defining query");
                            throw new LSClientException("Query could not be defined" + e.getMessage());
                        }
                    }

                }
                System.out.println("Came here");
            } else {
                //if no list exists, create empty query
                Query query = new Query();
                Subscriber subscriber = new Subscriber(server, query);
                subscriber.addListener(this);

                //get the initial set of records before starting subscribe
                try {
                    getRecords(query, server);
                } catch (ParserException e) {
                    LOG.error("net.es.lookup.pubsub.client.ArchiveService.start: Error parsing query results - " + e.getMessage());
                } catch (QueryException e) {
                    LOG.error("net.es.lookup.pubsub.client.ArchiveService.start: Error processing query - " + e.getMessage());
                }
                System.out.println("Came here");
                subscriber.startSubscription();
                subscribers.add(subscriber);


            }

        }

        LOG.info("net.es.lookup.pubsub.client.ArchiveService.start: Created and initialized " + subscribers.size() + " subscriber connections");


    }

    private void getRecords(Query query, SimpleLS server) throws LSClientException, QueryException, ParserException {


        QueryClient queryClient = new QueryClient(server);
        queryClient.setQuery(query);
        List<Record> results = queryClient.query();
        for (Record record : results) {
            try {
                forceSave(record);
            } catch (DuplicateEntryException e) {
                LOG.error("net.es.lookup.pubsub.client.ArchiveService.getRecords: Error inserting record to DB - " + e.getMessage());
            } catch (DatabaseException e) {
                LOG.error("net.es.lookup.pubsub.client.ArchiveService.getRecords: Error inserting record to DB - " + e.getMessage());
            }
        }


    }


    public void stop() throws LSClientException {

        LOG.info("net.es.lookup.pubsub.client.ArchiveService.stop: Stopping " + subscribers.size() + " subscriber connections");
        for (Subscriber subscriber : subscribers) {
            subscriber.removeListener(this);
            subscriber.stopSubscription();
        }

        LOG.info("net.es.lookup.pubsub.client.ArchiveService.stop: Stopped " + subscribers.size() + " subscriber connections");

    }

    public void onRecord(Record record) {

        LOG.info("net.es.lookup.pubsub.client.ArchiveService.onRecord: Processing Received message");
        try {
            save(record);
        } catch (DuplicateEntryException e) {
            LOG.error("net.es.lookup.pubsub.client.ArchiveService.onRecord: Error saving record" + e.getMessage());
        } catch (DatabaseException e) {
            LOG.error("net.es.lookup.pubsub.client.ArchiveService.onRecord: Error saving record" + e.getMessage());
        }
    }

    /**
     * This method saves record to database based on the state of the record.
     * For example: If a record-state: "registered", a new record will be created in DB. If the value is
     * "renew", "deleted"or "expired", the state wll be updated in the existing record.
     * If unable to update, then an exception will be thrown.
     *
     * @param record The record to be save or updated
     */
    private void save(Record record) throws DuplicateEntryException, DatabaseException {

        if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_REGISTER)) {
            LOG.info("net.es.lookup.pubsub.client.ArchiveService.save: insert as new record");
            Message message = new Message(record.getMap());
            Map<String, Object> keyValues = record.getMap();
            Message operators = new Message();
            Message query = new Message();

            Iterator it = keyValues.entrySet().iterator();
            LOG.info("net.es.lookup.pubsub.client.ArchiveService.save: Constructing query based on message");
            while (it.hasNext()) {

                Map.Entry<String, Object> pairs = (Map.Entry) it.next();
                operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
                query.add(pairs.getKey(), pairs.getValue());

            }
            LOG.info("net.es.lookup.pubsub.client.ArchiveService.save: Check and insert record");

            db.queryAndPublishService(message, query, operators);

            LOG.info("net.es.lookup.pubsub.client.ArchiveService.save: Inserted record");

        } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_RENEW)) {
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            LOG.info("net.es.lookup.pubsub.client.ArchiveService.save: renew existing record");

            db.updateService(recordUri, message);

        } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_EXPIRE)) {
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            LOG.info("net.es.lookup.pubsub.client.ArchiveService.save: received expired record");

            db.updateService(recordUri, message);

        } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_DELETE)) {
            String recordUri = record.getURI();
            Message message = new Message(record.getMap());
            LOG.info("net.es.lookup.pubsub.client.ArchiveService.save: received deleted record");

            db.updateService(recordUri, message);

        }
    }

    /**
     * This method will only create a new entry for the record.
     * If an entry already exists, the insert operation is ignored and method returns.
     *
     * @param record The record to be save or updated
     */
    private void forceSave(Record record) throws DuplicateEntryException, DatabaseException {

        LOG.info("net.es.lookup.pubsub.client.ArchiveService.forceSave: insert as new record");
        Message message = new Message(record.getMap());
        Map<String, Object> keyValues = record.getMap();
        Message operators = new Message();
        Message query = new Message();

        Iterator it = keyValues.entrySet().iterator();
        LOG.info("net.es.lookup.pubsub.client.ArchiveService.forceSave: Constructing query based on message");
        while (it.hasNext()) {

            Map.Entry<String, Object> pairs = (Map.Entry) it.next();
            operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
            query.add(pairs.getKey(), pairs.getValue());

        }
        LOG.info("net.es.lookup.pubsub.client.ArchiveService.forceSave: Check and insert record");

        db.queryAndPublishService(message, query, operators);

        LOG.info("net.es.lookup.pubsub.client.ArchiveService.forceSave: Inserted record");
    }
}
