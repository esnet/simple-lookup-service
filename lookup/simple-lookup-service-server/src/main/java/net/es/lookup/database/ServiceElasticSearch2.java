//package net.es.lookup.database;
//
//import com.mongodb.*;
//import com.mongodb.bulk.BulkWriteResult;
//import com.mongodb.client.FindIterable;
//import com.mongodb.client.MongoCursor;
//import com.mongodb.client.model.BulkWriteOptions;
//import com.mongodb.client.model.FindOneAndUpdateOptions;
//import com.mongodb.client.model.ReturnDocument;
//import com.mongodb.client.model.UpdateOneModel;
//import com.mongodb.client.result.DeleteResult;
//import net.es.lookup.common.Message;
//import net.es.lookup.common.ReservedKeys;
//import net.es.lookup.common.ReservedValues;
//import net.es.lookup.common.exception.internal.DatabaseException;
//import net.es.lookup.common.exception.internal.DuplicateEntryException;
//import net.es.lookup.common.exception.internal.RecordNotFoundException;
//import org.apache.http.HttpHost;
//import org.bson.Document;
//import org.elasticsearch.action.get.GetRequest;
//import org.elasticsearch.action.index.IndexRequest;
//import org.elasticsearch.action.update.UpdateRequest;
//import org.elasticsearch.action.update.UpdateResponse;
//import org.elasticsearch.client.RequestOptions;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.elasticsearch.common.xcontent.XContentType;
//import org.elasticsearch.index.get.GetResult;
//import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
//import org.joda.time.DateTime;
//import org.joda.time.format.DateTimeFormatter;
//import org.joda.time.format.ISODateTimeFormat;
//import org.quartz.DisallowConcurrentExecution;
//
//import java.io.IOException;
//import java.util.*;
//import java.util.Map.Entry;
//import java.util.regex.Pattern;
//
//import static com.mongodb.client.model.Filters.*;
//
//@DisallowConcurrentExecution
//public class ServiceElasticSearch2 {
//
//    private String dburl = "127.0.0.1";
//    private int dbport1 = 9200;
//    private int dbport2 = 9201;
//    private String indexName = "LookupService";
////    private String collname = "services";
//
//    private static ServiceElasticSearch2 instance = null;
//    RestHighLevelClient client = null;
//
////  private MongoClient mongo;
////  private MongoDatabase db;
////  private MongoCollection coll;
//
//    private static Map<String, String> operatorMapping = new HashMap<String, String>();
//    private static Map<String, String> listOperatorMapping = new HashMap<String, String>();
//    private static Map<String, String> wildCardMapping = new HashMap<String, String>();
//
//    {
//        operatorMapping.put(ReservedValues.RECORD_OPERATOR_ALL, "$and");
//        operatorMapping.put(ReservedValues.RECORD_OPERATOR_ANY, "$or");
//
//        listOperatorMapping.put(ReservedValues.RECORD_OPERATOR_ANY, "$in");
//        listOperatorMapping.put(ReservedValues.RECORD_OPERATOR_ALL, "$all");
//
//        wildCardMapping.put("prefix", "^");
//        wildCardMapping.put("suffix", "$");
//    }
//
//    public static ServiceElasticSearch2 getInstance() {
//
//        return ServiceElasticSearch2.instance;
//    }
//
//    /**
//     * Constructor uses default url and port - mongodb running on localhost and default port - 27017.
//     * Creates a new connection if it cannot find one
//     */
//    public ServiceElasticSearch2() throws DatabaseException {
//        init();
//    }
//
//    /**
//     * Constructor to create a new db connection if it cannot find one If db connection is found, it
//     * retrieves the db and collection(table).
//     *
//     * @param dburl     the url to connect to database
//     * @param dbport1   First DB port
//     * @param dbport2   Second DB port
//     * @param indexName Name of the elastic search index
//     */
//    public ServiceElasticSearch2(String dburl, int dbport1, int dbport2, String indexName)
//            throws DatabaseException {
//
//        this.dburl = dburl;
//        this.dbport1 = dbport1;
//        this.dbport2 = dbport2;
//        this.indexName = indexName;
//        init();
//    }
//
//    private void init() throws DatabaseException {
//
//        if (ServiceElasticSearch2.instance != null) {
//
//            // An instance has been already created.
//            throw new DatabaseException("Attempt to create a second instance of elastic search");
//        }
//
//        ServiceElasticSearch2.instance = this;
//
//        try {
//            client =
//                    new RestHighLevelClient(
//                            RestClient.builder(
//                                    new HttpHost(this.dburl, this.dbport1, "http"), new HttpHost(dburl, dbport2, "http")));
//
//        } catch (Exception e) {
//
//            throw new DatabaseException(e.getMessage());
//        }
//    }
//
//    /**
//     * Inserts records into the database. This method checks if a record exists before inserting it
//     * into the database. If record exists then a DuplicateEntryException is thrown.
//     * What is operator??
//     *
//     * @param message      The record to be inserted
//     * @return Returns the record that was inserted as a Message
//     * @throws DatabaseException       if error while writing to database
//     * @throws DuplicateEntryException if record is already present
//     */
//    public Message queryAndPublishService(Message message,String id)
//            throws DuplicateEntryException, IOException {
//
//        //checking for existing record
//        boolean exists;
//        try {
//            GetRequest getRequest = new GetRequest(indexName, id);
//            getRequest.fetchSourceContext(new FetchSourceContext(false));
//            getRequest.storedFields("_none_");
//            exists = client.exists(getRequest, RequestOptions.DEFAULT);
//        } catch (IOException e) {
//            throw new IOException("IOException");
//        }
//        if (exists) {
//            throw new DuplicateEntryException("Record already exists");
//        }
//        //if record doesnt already exists
//        IndexRequest request = new IndexRequest(indexName);
//        request.id(id);
//        request.source(message, XContentType.JSON);
//
//        //What is this for??
//        Message timestampedMessage = addTimestamp(message);
//        Map<String, Object> services = timestampedMessage.getMap();
//        Document doc = new Document();
//        doc.putAll(services);
//
//
//        return toMessage(doc);
//    }
//
//    /**
//     * This method updates an existing record in the database.
//     *
//     * @param serviceid     The unique service identifier
//     * @param updateRequest The fields to be modified
//     * @return The record that was modified (after modification) as a Message
//     * @throws DatabaseException if error updating record
//     */
//    public Message updateService2(String serviceid, Message updateRequest) throws DatabaseException {
//
//        Message response = new Message();
//
//        if (serviceid != null && !serviceid.isEmpty()) {
//
//            Document query = new Document();
//            query.put(ReservedKeys.RECORD_URI, serviceid);
//
//            Message timestampedMessage = addTimestamp(updateRequest);
//
//            Document updateObject = new Document("$set", new Document(timestampedMessage.getMap()));
//            try {
//                FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions();
//                updateOptions.returnDocument(ReturnDocument.AFTER);
//
//                Document result = (Document) coll.findOneAndUpdate(query, updateObject, updateOptions);
//
//                if (result != null) {
//                    response = toMessage(result);
//                } else {
//                    throw new DatabaseException("Error renewing record");
//                }
//            } catch (MongoException e) {
//                throw new DatabaseException(e.getMessage());
//            }
//        } else {
//            throw new DatabaseException("Record URI not specified!!!");
//        }
//
//        return response;
//    }
//
//
//    /**
//     * This method updates an existing record in the database.
//     *
//     * @param serviceid     The unique service identifier
//     * @param updateRequest The fields to be modified
//     * @return The record that was modified (after modification) as a Message
//     * @throws DatabaseException if error updating record
//     */
//    public Message updateService(String serviceid, Message updateRequest) throws DatabaseException, IOException{
//
//        Message response;
//
//
//        if (serviceid != null && !serviceid.isEmpty()) {
//
//            UpdateRequest request = new UpdateRequest(indexName, serviceid);
//            request.doc(updateRequest, XContentType.JSON);
//            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
//            GetResult result = updateResponse.getGetResult();
//            if (result.isExists()) {
//                Map sourceAsMap = result.sourceAsMap();
//                response = new Message(sourceAsMap);
//            } else {
//                throw new DatabaseException("Error renewing record");
//            }
//            response = addTimestamp(response);
//        } else {
//            throw new DatabaseException("Record URI not specified!!!");
//        }
//
//        return response;
//    }
//
//    public List<Message> query(Message message, Message queryRequest, Message operators)
//            throws DatabaseException {
//
//        return this.query(message, queryRequest, operators, 0);
//    }
//
//    /**
//     * Method to query records from database.
//     *
//     * @param message      original query request
//     * @param queryRequest query keywords extracted from the priginal request
//     * @param operators    operators like ANY, ALL that specifies how query keywords should be applied
//     * @param maxResults   max results to be returned. not implemented
//     * @return List of all the records
//     */
//    public List<Message> query(
//            Message message, Message queryRequest, Message operators, int maxResults)
//            throws DatabaseException {
//
//        Document query;
//
//        if (queryRequest.getMap().isEmpty()) {
//            query = new Document();
//        } else {
//            query = buildQuery(queryRequest, operators);
//        }
//
//        ArrayList<Message> result = new ArrayList<Message>();
//
//        try {
//            FindIterable resultIterator = coll.find(query);
//            MongoCursor cursor = resultIterator.iterator();
//
//            while (cursor.hasNext()) {
//                Document tmp = (Document) cursor.next();
//                Message dbObjectMessage = toMessage(tmp);
//                result.add(dbObjectMessage);
//                tmp = null;
//            }
//
//        } catch (MongoException e) {
//
//            throw new DatabaseException("Error retrieving results");
//        }
//
//        return result;
//    }
//
//    /**
//     * Method to query all the records from the database.
//     *
//     * @return list of all records.
//     */
//    public List<Message> queryAll() throws DatabaseException {
//
//        Message msg = new Message();
//        return query(msg, msg, msg);
//    }
//
//    /**
//     * Builds the MongoDB query from the given map. The map is received from the REST API. All the
//     * operators and the wildcard characters have to be converted to Mongo's wildcard characters and
//     * operators.
//     *
//     * @param queryRequest The map containing the query key-values
//     * @param operators    The map containing operators
//     * @return Mongo Document object containing the query
//     */
//    public Document buildQuery(Message queryRequest, Message operators) {
//
//        Map<String, Object> queryMap = queryRequest.getMap();
//        Map<String, String> opsMap = operators.getMap();
//
//        List<HashMap<String, Object>> keyValueList = new ArrayList<HashMap<String, Object>>();
//
//        for (String queryKey : queryMap.keySet()) {
//
//            HashMap<String, Object> tmpHash = new HashMap<String, Object>();
//            Object queryValue = queryMap.get(queryKey);
//
//            if (queryValue instanceof String) {
//
//                String val = (String) queryValue;
//                // deal with metacharacter
//                if (val.contains("*")) {
//                    Pattern value = getMongoWildCardPattern(val);
//                    tmpHash.put(queryKey, value);
//                } else {
//                    tmpHash.put(queryKey, (String) queryValue);
//                }
//
//            } else if (queryValue instanceof List) {
//
//                List<Object> values = (List<Object>) queryValue;
//                ArrayList newValues = new ArrayList();
//
//                if (values.size() > 1) {
//
//                    for (int i = 0; i < values.size(); i++) {
//                        String val = (String) values.get(i);
//                        if (val.contains("*")) {
//                            Pattern value = getMongoWildCardPattern(val);
//                            newValues.add(value);
//                        } else {
//                            newValues.add(val);
//                        }
//                    }
//                    tmpHash.put(queryKey, newValues);
//
//                    HashMap<String, Object> listvalues = new HashMap<String, Object>();
//
//                    if (opsMap.containsKey(queryKey)
//                            && listOperatorMapping.containsKey(opsMap.get(queryKey).toLowerCase())) {
//                        // get the operator
//                        String curop = listOperatorMapping.get(opsMap.get(queryKey).toLowerCase());
//                        listvalues.put(curop, newValues);
//                        tmpHash.put(queryKey, listvalues);
//                    } else {
//                        tmpHash.put(queryKey, newValues);
//                    }
//
//                } else if (values.size() == 1) {
//
//                    String val = (String) values.get(0);
//                    if (val.contains("*")) {
//                        Pattern value = getMongoWildCardPattern(val);
//                        tmpHash.put(queryKey, value);
//                    } else {
//                        tmpHash.put(queryKey, values.get(0));
//                    }
//                }
//            }
//
//            if (!tmpHash.isEmpty()) {
//                keyValueList.add(tmpHash);
//            }
//        }
//
//        Document query = new Document();
//        String queryOp = operators.getOperator();
//        String op;
//
//        if (queryOp == null || queryOp.isEmpty()) {
//            op = ReservedValues.RECORD_OPERATOR_DEFAULT; // uses only the first value from the list
//        } else {
//            op = queryOp.toLowerCase();
//        }
//
//        String mongoOp = "";
//        if (operatorMapping.containsKey(op)) {
//            mongoOp = operatorMapping.get(op);
//        }
//
//        if (!keyValueList.isEmpty()) {
//            query.put(mongoOp, keyValueList);
//        }
//        return query;
//    }
//
//    /**
//     * This is a private method that retrieves the mongo wild card pattern. The REST API of sLS
//     * supports only prefix and suffix matching using the '*' character. Whereas, mongo uses PCRE
//     * standards - '^' and '$'. This method maps prefix and suffix received from the REST API to the
//     * Mongo wild card operation
//     *
//     * @param stringPattern The string pattern received from the REST API
//     * @return Pattern object containing the Mongo's PCRE
//     */
//    private Pattern getMongoWildCardPattern(String stringPattern) {
//
//        Pattern newPattern = null;
//
//        if (stringPattern.endsWith("*")) {
//            newPattern = Pattern.compile("^" + stringPattern.substring(0, stringPattern.length() - 1));
//        } else if (stringPattern.startsWith("*")) {
//            newPattern = Pattern.compile(stringPattern.substring(1, stringPattern.length()) + "$");
//        }
//
//        return newPattern;
//    }
//
//    /**
//     * This method retrieves the record from the database using the record uri.
//     *
//     * @param recorduri the uri of the record
//     * @return Entire record as a Message object
//     * @throws DatabaseException thrown if error accessing the record.
//     */
//    public Message getRecordByUri(String recorduri) throws DatabaseException {
//
//        Document query = new Document();
//        query.put(ReservedKeys.RECORD_URI, recorduri);
//        Message result = null;
//
//        try {
//
//            FindIterable resultIterator = coll.find(query);
//            MongoCursor cursor = resultIterator.iterator();
//
//            // must have only one record. In case there are multiple records, the API can return only one
//            // record so get the first one
//            if (cursor.hasNext()) {
//                result = toMessage((Document) cursor.next());
//            }
//
//        } catch (MongoException e) {
//
//            throw new DatabaseException(e.getMessage());
//        }
//
//        return result;
//    }
//
//    /**
//     * Inserts the given record into the database.
//     *
//     * @param message The record to be added to the database
//     * @throws DatabaseException thrown if error writing to database
//     */
//    public void publishService(Message message) throws DatabaseException {
//
//        try {
//
//            Document document = new Document(message.getMap());
//
//            coll.insertOne(document);
//        } catch (MongoException e) {
//
//            throw new DatabaseException(e.getMessage());
//        }
//    }
//
//    /**
//     * Returns the number of records in the database.
//     *
//     * @return number of records in the database
//     * @throws DatabaseException thrown if error accessing database
//     */
//    public long getCount() throws DatabaseException {
//
//        try {
//            return coll.count();
//        } catch (MongoException e) {
//            throw new DatabaseException(e.getMessage());
//        }
//    }
//
//    /**
//     * This method deletes the record for given uri.
//     *
//     * @param recorduri uri of the record
//     * @return Message - returns the deleted record as a Message object
//     * @throws DatabaseException thrown if error deleting record
//     */
//    public Message deleteRecord(String recorduri) throws DatabaseException, RecordNotFoundException {
//
//        Document query = new Document();
//        query.put(ReservedKeys.RECORD_URI, recorduri);
//
//        // retrieve the record to send it back in the response
//        Message response = getRecordByUri(recorduri);
//
//        if (response != null && response.getMap() != null && !response.getMap().isEmpty()) {
//            try {
//
//                DeleteResult deleteResult = coll.deleteOne(query);
//                if (deleteResult.getDeletedCount() != 1) {
//                    throw new DatabaseException("Error deleting record");
//                }
//            } catch (MongoException e) {
//                throw new DatabaseException(e.getMessage());
//            }
//
//        } else {
//            throw new RecordNotFoundException("Record not found in the database");
//        }
//
//        return response;
//    }
//
//    /**
//     * This method deletes expired records from db and returns number of records deleted.
//     *
//     * @param datetime All records that have _timestamp before "datetime" are deleted
//     * @return number of records deleted
//     * @throws DatabaseException thrown if error deleting records from database
//     */
//    public long deleteExpiredRecords(Date datetime) throws DatabaseException {
//
//        try {
//            DeleteResult result = coll.deleteMany(lte("_timestamp", datetime));
//            return result.getDeletedCount();
//        } catch (MongoException e) {
//
//            throw new DatabaseException(e.getMessage());
//        }
//    }
//
//    /**
//     * This method deletes all the content in the DB and returns number of records deleted.
//     *
//     * @return long - number of records deleted
//     * @throws DatabaseException thrown if error deleting records
//     */
//    public long deleteAllRecords() throws DatabaseException {
//
//        try {
//
//            DeleteResult deleteResult = coll.deleteMany(new Document());
//            return deleteResult.getDeletedCount();
//        } catch (MongoException e) {
//
//            throw new DatabaseException(e.getMessage());
//        }
//    }
//
//    private Message toMessage(Document doc) {
//        Message result;
//        if (doc != null) {
//            doc.remove("_timestamp");
//            doc.remove("_id");
//            doc.remove("_lastUpdated");
//            result = new Message(doc);
//        } else {
//            result = new Message();
//        }
//        return result;
//    }
//
//    /**
//     * This method handles updates to multiple records.
//     *
//     * @param records Map of uri and the record to be updates
//     * @return Message returns a message with the number of records updated
//     * @throws DatabaseException exception is thrown if error updating database
//     */
//    public Message bulkUpdate(Map<String, Message> records) throws DatabaseException {
//
//        List<UpdateOneModel> bulkUpdateOperations = new ArrayList<>();
//        for (Entry<String, Message> recordEntry : records.entrySet()) {
//            String recordId = recordEntry.getKey();
//            Message fullRecord = recordEntry.getValue();
//
//            Document query = new Document();
//            query.put(ReservedKeys.RECORD_URI, recordId);
//
//            Message mongoTimestampedMessage = addTimestamp(fullRecord);
//
//            Document updateObject = new Document("$set", new Document(mongoTimestampedMessage.getMap()));
//
//            UpdateOneModel updateOperation = new UpdateOneModel(query, updateObject);
//            bulkUpdateOperations.add(updateOperation);
//        }
//
//        Message response = new Message();
//
//        if (bulkUpdateOperations.size() > 0) {
//
//            BulkWriteResult bulkUpdateResult =
//                    coll.bulkWrite(bulkUpdateOperations, new BulkWriteOptions().ordered(false));
//            int modifiedRecordsCount = 0;
//            if (bulkUpdateResult.isModifiedCountAvailable()) {
//                modifiedRecordsCount = bulkUpdateResult.getModifiedCount();
//            }
//
//            if (modifiedRecordsCount < bulkUpdateOperations.size()) {
//
//                throw new DatabaseException("Error updating records");
//            }
//
//            response.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, modifiedRecordsCount);
//        }
//
//        return response;
//    }
//
//    /**
//     * Finds records that were updated between start and end date.
//     *
//     * @param start Start timestamp as Date object
//     * @return List list of records that are between the start and end time
//     * @params end End timestamp as Date object
//     */
//    public List<Message> findRecordsInTimeRange(Date start, Date end) throws DatabaseException {
//
//        List<Message> result = new ArrayList<Message>();
//
//        try {
//            FindIterable resultIterator =
//                    coll.find(and(gt("_lastUpdated", start), lte("_lastUpdated", end)));
//            MongoCursor cursor = resultIterator.iterator();
//
//            while (cursor.hasNext()) {
//                Document tmp = (Document) cursor.next();
//                Message dbObjectMessage = toMessage(tmp);
//                result.add(dbObjectMessage);
//                tmp = null;
//            }
//        } catch (MongoException e) {
//
//            throw new DatabaseException(e.getMessage());
//        }
//        return result;
//    }
//
//    private Message addTimestamp(Message message) {
//
//        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
//        DateTime dt = fmt.parseDateTime(message.getExpires());
//
//        Date timestamp = dt.toDate();
//        message.add("_timestamp", timestamp);
//        message.add("_lastUpdated", new Date());
//        return message;
//    }
//}
