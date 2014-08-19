package net.es.lookup.pubsub.client;

import net.es.lookup.client.QueryClient;
import net.es.lookup.client.SimpleLS;
import net.es.lookup.client.Subscriber;
import net.es.lookup.client.SubscriberListener;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.LSClientException;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.DBPool;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.pubsub.Publisher;
import net.es.lookup.pubsub.client.failover.FailedConnection;
import net.es.lookup.pubsub.client.failover.FailureRecovery;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;
import org.apache.log4j.Logger;
import org.joda.time.Instant;

import java.net.URI;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 11/6/13
 * Time: 2:59 PM
 * <p/>
 * <p/>
 * This class defines a cache. A cache is a collection of connectedSubscribers.
 */
public class Cache implements SubscriberListener {

    private String name;
    private String type;
    private List<Publisher> publishers;

    private List<Subscriber> connectedSubscribers;
    private List<SubscriberListener> subscriberListeners;

    private Instant lastCacheRestartTime;

    //private List<FailedConnection> failedConnectionList;


    private FailureRecovery failureRecovery;


    public FailureRecovery getFailureRecovery() {

        return failureRecovery;
    }

    //FailureRecovery subscriberFailureRecovery;
    private static Logger LOG = Logger.getLogger(Cache.class);


    public Cache(String name, String type, List<Publisher> publishers) throws LSClientException {

        this.name = name;
        this.type = type;
        this.publishers = publishers;

        connectedSubscribers = new LinkedList<Subscriber>();
        subscriberListeners = new LinkedList<SubscriberListener>();
        failureRecovery = new FailureRecovery();

        initialize();

    }


    public String getName() {

        return name;
    }

    public String getType() {

        return type;
    }

    public List<Subscriber> getSubscribers() {

        return connectedSubscribers;
    }


    private void initialize() throws LSClientException {

        int count = publishers.size();
        LOG.info("net.es.lookup.pubsub.client.Cache: Initializing " + count + " publishers");
        List<Map<String, Object>> serverQueries = new LinkedList<Map<String, Object>>();
        for (int i = 0; i < count; i++) {
            try {

                Publisher source = publishers.get(i);
                URI uri = source.getAccesspoint();

                String subscribeRelativeUrl = uri.getPath();
                String host = uri.getHost();
                int port = uri.getPort();

                SimpleLS server = new SimpleLS(host, port);
                List<Map<String, Object>> queryList = source.getQueries();

                if (queryList != null && !queryList.isEmpty()) {
                    for (Map<String, Object> queryMap : queryList) {
                        Query query = new Query(queryMap);
                        Subscriber subscriber = new Subscriber(server, query, subscribeRelativeUrl);
                        subscriber.addListener(this);
                        connectedSubscribers.add(subscriber);
                    }
                }


            } catch (QueryException e) {
                LOG.error("net.es.lookup.pubsub.client.Cache: Error creating query from the given key-value pair");
                throw new LSClientException("net.es.lookup.pubsub.client.CacheService: Error initializing subscribe hosts -" + e.getMessage());
            }
        }

    }

    /**
     * This methods starts the subscription connection. The cache for sLS will first do a pull request on the query to get the current
     * set of records and then will start subscription
     */
    public void start() throws LSClientException {

        LOG.info("net.es.lookup.pubsub.client.Cache.start: Starting the subscriber connections");
        try {
            emptyCache();
        } catch (DatabaseException e) {
            LOG.error("net.es.lookup.pubsub.client.Cache.start: Error emptying cache - " + e.getMessage());
        }
        int index = 0;
        for (Subscriber subscriber : connectedSubscribers) {

            Query query = subscriber.getQuery();
            SimpleLS server = subscriber.getServer();

            try {
                getRecords(query, server);
            } catch (ParserException e) {
                LOG.error("net.es.lookup.pubsub.client.Cache.start: Error parsing initial query results - " + e.getMessage());
            } catch (QueryException e) {
                LOG.error("net.es.lookup.pubsub.client.Cache.start: Error creating initial query - " + e.getMessage());
            }

            subscriber.startSubscription();
            lastCacheRestartTime = new Instant();

        }

        LOG.info("net.es.lookup.pubsub.client.Cache.start: Created and initialized " + connectedSubscribers.size() + " subscriber connections");


    }

    /**
     * This method stops the subscription.
     */

    public void stop() throws LSClientException {

        LOG.info("net.es.lookup.pubsub.client.Cache.stop: Stopping " + connectedSubscribers.size() + " subscriber connections");
        for (Subscriber subscriber : connectedSubscribers) {
            subscriber.removeListener(this);
            subscriber.stopSubscription();
        }

        LOG.info("net.es.lookup.pubsub.client.Cache.stop: Stopped " + connectedSubscribers.size() + " subscriber connections");

    }

    /**
     * This method stops the subscription.
     */

    public void restart() throws LSClientException {

        stop();
        start();

    }

    /**
     * This method queries the publisher and retrieves the initial set of records
     * matching the query.
     */
    private void getRecords(Query query, SimpleLS server) throws LSClientException, QueryException, ParserException {

        QueryClient queryClient = new QueryClient(server);
        queryClient.setQuery(query);
        List<Record> results = queryClient.query();
        for (Record record : results) {
            try {
                forceSave(record);
            } catch (DuplicateEntryException e) {
                LOG.error("net.es.lookup.pubsub.client.Cache.getRecords: Error inserting record to DB - " + e.getMessage());
            } catch (DatabaseException e) {
                LOG.error("net.es.lookup.pubsub.client.Cache.getRecords: Error inserting record to DB - " + e.getMessage());
            }
        }


    }

    /**
     * Every subscriber listener is expected to implement the onRecord method. This method defines how to deal with incoming records
     */

    public void onRecord(Record record) throws LSClientException {

        LOG.info("net.es.lookup.pubsub.client.Cache.onRecord: Processing Received message");
        LOG.debug("net.es.lookup.pubsub.client.Cache.onRecord" + record.getRecordType());
        if (record.getRecordType().equals(ReservedValues.RECORD_VALUE_TYPE_ERROR)) {
            String subscriberUrl = (String) record.getValue(ReservedKeys.SUBSCRIBER);
            String queue = (String) record.getValue(ReservedKeys.QUEUE);
            String queueUrl = (String) record.getValue(ReservedKeys.QUEUE_URL);
            LOG.debug("net.es.lookup.pubsub.client.Cache.onRecord:" + record.getValue(ReservedKeys.ERROR_MESSAGE));
            if (subscriberUrl == null) {
                LOG.debug("net.es.lookup.pubsub.client.Cache.onRecord: Subscriber null");
            }
            Subscriber toBeRemoved = null;
            LOG.debug("net.es.lookup.pubsub.client.Cache.onRecord: About to find subscriber from the active connections list");
            for (Subscriber s : connectedSubscribers) {
                if (s.getSubscribeRequestUrl().equals(subscriberUrl) && s.getQueue().equals(queue) && s.getQueueUrl().equals(queueUrl)) {
                    toBeRemoved = s;
                }
            }
            if (toBeRemoved != null) {

                boolean removedFromActiveList = this.connectedSubscribers.remove(toBeRemoved);
                if (removedFromActiveList) {

                    toBeRemoved.stopSubscription();
                    FailedConnection failedConnection = new FailedConnection(toBeRemoved);
                   // toBeRemoved.addListener(this);
                    failureRecovery.addFailedConnection(failedConnection);

                    LOG.error("net.es.lookup.pubsub.client.Cache.onRecord: Tore down subscriber connection. Added subscriber to failed connection");
                    LOG.debug("net.es.lookup.pubsub.client.Cache.onRecord: Number of failed connections: "+ failureRecovery.getFailedConnectionCount());
                } else {
                    throw new LSClientException("net.es.lookup.pubsub.client.Cache.onRecord: Failed to remove failedConnection from active list. Exiting");
                }

            } else {
                throw new LSClientException("net.es.lookup.pubsub.client.Cache.onRecord: Cannot find subscriber. Cannot run failure recovery on this subscriber");
            }


        } else {
            try {
                save(record);
            } catch (DuplicateEntryException e) {
                LOG.error("net.es.lookup.pubsub.client.Cache.onRecord: Error saving record" + e.getMessage());
            } catch (DatabaseException e) {
                LOG.error("net.es.lookup.pubsub.client.Cache.onRecord: Error saving record" + e.getMessage());
            }
        }
    }


    /**
     * This method saves record to database based on the state of the record and the type of cache.
     * For example: If a record-state: "registered", a new record will be created in DB. If the value is
     * "renew", "deleted"or "expired", the state wll be updated in the existing record.
     * If unable to update, then an exception will be thrown.
     *
     * @param record The record to be save or updated
     */
    private void save(Record record) throws DuplicateEntryException, DatabaseException {

        ServiceDAOMongoDb db = DBPool.getDb(this.name);

        //register and renew are dealt the same way in both archive and replication cache If it changes for other types of cache,
        //reorganize the code in this section

        Message message = new Message(record.getMap());
        Map<String, Object> keyValues = record.getMap();
        Message operators = new Message();
        Message query = new Message();

        Iterator it = keyValues.entrySet().iterator();
        LOG.info("net.es.lookup.pubsub.client.Cache.save: Constructing query based on message");
        while (it.hasNext()) {

            Map.Entry<String, Object> pairs = (Map.Entry) it.next();
            operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
            query.add(pairs.getKey(), pairs.getValue());

        }

        if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_REGISTER)) {
            LOG.info("net.es.lookup.pubsub.client.Cache.save: insert as new record");

            db.queryAndPublishService(message, query, operators);

            LOG.info("net.es.lookup.pubsub.client.Cache.save: Inserted record");

        } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_RENEW)) {


            String recordUri = record.getURI();
            Message renewMessage = new Message(record.getMap());
            LOG.info("net.es.lookup.pubsub.client.Cache.save: renew existing record");

            try{
                db.updateService(recordUri, renewMessage);
            }catch (DatabaseException dbe){
                db.queryAndPublishService(message, query, operators);
            }

            LOG.info("net.es.lookup.pubsub.client.Cache.save: Inserted renew record");

        }

        if (this.type.equals(ReservedValues.CACHE_TYPE_ARCHIVE)) {
            if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_EXPIRE)) {
                String recordUri = record.getURI();
                Message expiredMessage = new Message(record.getMap());
                LOG.info("net.es.lookup.pubsub.client.Cache.save: Archiving expired record");

                try{
                    db.updateService(recordUri, expiredMessage);
                }catch (DatabaseException dbe){
                    db.queryAndPublishService(message, query, operators);
                }

                LOG.info("net.es.lookup.pubsub.client.Cache.save: Inserted expired record");


            } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_DELETE)) {
                String recordUri = record.getURI();
                Message deletedMessage = new Message(record.getMap());
                LOG.info("net.es.lookup.pubsub.client.Cache.save: Archiving 'deleted' record");

                try {
                    db.updateService(recordUri, deletedMessage);
                }catch(DatabaseException dbe){
                    db.queryAndPublishService(message, query, operators);
                }

                LOG.info("net.es.lookup.pubsub.client.Cache.save: Inserted deleted record");
            }
        } else if (this.type.equals(ReservedValues.CACHE_TYPE_REPLICATION)) {
            if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_EXPIRE)) {
                String recordUri = record.getURI();
                //Message expiredMessage = new Message(record.getMap());
                LOG.info("net.es.lookup.pubsub.client.Cache.save: Cache type is replication. Deleting 'expired' record");

                try{
                    db.deleteService(recordUri);
                }catch (DatabaseException dbe){
                    LOG.debug("net.es.lookup.pubsub.client.Cache.save: Did not find record");
                }

            } else if (record.getRecordState().equals(ReservedValues.RECORD_VALUE_STATE_DELETE)) {
                String recordUri = record.getURI();
                //Message deletedMessage = new Message(record.getMap());
                LOG.info("net.es.lookup.pubsub.client.Cache.save: Cache type is replication. Deleting 'deleted' record");

                try{
                    db.deleteService(recordUri);
                }catch (DatabaseException dbe){
                    LOG.debug("net.es.lookup.pubsub.client.Cache.save: Did not find record");
                }


            }
        }
    }


    /**
     * This method will only create a new entry for the record.
     * If an entry already exists, the insert operation is ignored and method returns.
     *
     * @param record The record to be save or updated
     */
    private void forceSave(Record record) throws DuplicateEntryException, DatabaseException {

        ServiceDAOMongoDb db = DBPool.getDb(this.name);

        LOG.info("net.es.lookup.pubsub.client.CacheService.forceSave: insert as new record");
        Message message = new Message(record.getMap());
        Map<String, Object> keyValues = record.getMap();
        Message operators = new Message();
        Message query = new Message();

        Iterator it = keyValues.entrySet().iterator();
        LOG.info("net.es.lookup.pubsub.client.CacheService.forceSave: Constructing query based on message");
        while (it.hasNext()) {

            Map.Entry<String, Object> pairs = (Map.Entry) it.next();
            operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
            query.add(pairs.getKey(), pairs.getValue());

        }
        LOG.info("net.es.lookup.pubsub.client.CacheService.forceSave: Check and insert record");

        db.queryAndPublishService(message, query, operators);

        LOG.info("net.es.lookup.pubsub.client.CacheService.forceSave: Inserted record");
    }

    /**
     * This method empties the contents of the cache.
     */
    private void emptyCache() throws DatabaseException {

        LOG.info("net.es.lookup.pubsub.client.CacheService.emptyCache: Emptying cache");
        ServiceDAOMongoDb db = DBPool.getDb(this.name);
        db.deleteAll();
        LOG.info("net.es.lookup.pubsub.client.CacheService.emptyCache: Emptied cache");
    }


    public List<Publisher> getPublishers() {

        return publishers;
    }

    public Instant getLastRestartedTimeStamp() {

        return lastCacheRestartTime;
    }
}
