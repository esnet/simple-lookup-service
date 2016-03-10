package net.es.lookup.database;


import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import com.mongodb.client.result.DeleteResult;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.common.exception.internal.RecordNotFoundException;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import java.util.*;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.lte;


public class ServiceDAOMongoDb {

    private String dburl = "127.0.0.1";
    private int dbport = 27017;
    private String dbname = "LookupService";
    private String collname = "services";

    private MongoClient mongo;
    private MongoDatabase db;
    private MongoCollection coll;

    private static Map<String, String> operatorMapping = new HashMap<String, String>();
    private static Map<String, String> listOperatorMapping = new HashMap<String, String>();
    private static Map<String, String> wildCardMapping = new HashMap<String, String>();

    {
        operatorMapping.put(ReservedValues.RECORD_OPERATOR_ALL, "$and");
        operatorMapping.put(ReservedValues.RECORD_OPERATOR_ANY, "$or");

        listOperatorMapping.put(ReservedValues.RECORD_OPERATOR_ANY, "$in");
        listOperatorMapping.put(ReservedValues.RECORD_OPERATOR_ALL, "$all");

        wildCardMapping.put("prefix", "^");
        wildCardMapping.put("suffix", "$");

    }

    //retrieves the db and collection(table); creates a new one if it cannot find one
    public ServiceDAOMongoDb(String dburl, int dbport, String dbname, String collname) throws DatabaseException {

        this.dburl = dburl;
        this.dbport = dbport;
        this.dbname = dbname;
        this.collname = collname;
        init();

    }


    private void init() throws DatabaseException {

        if (DBPool.containsKey(dbname)) {

            // An instance has been already created.
            throw new DatabaseException("Attempt to create a second instance of ServiceDAOMongoDb");

        }

        DBPool.addDb(dbname, this);

        try {

            mongo = new MongoClient(dburl, dbport);
            db = mongo.getDatabase(dbname);
            coll = db.getCollection(collname);
            coll.count();

        } catch (MongoException e) {

            throw new DatabaseException(e.getMessage());

        }


    }


    /**
     * Inserts records into the database. This method checks if a record exists before inserting it into the database.
     * If record exists then a DuplicateEntryException is thrown.
     *
     * @param message  The record to be inserted
     * @param queryRequest Query to check if the record already exists
     * @param operators The operation (ALL|ANY) to be performed on the query. Default is ALL
     * @return          Returns the record that was inserted as a Message
     * @throws DatabaseException
     * @throws DuplicateEntryException
     */
    public Message queryAndPublishService(Message message, Message queryRequest, Message operators) throws DatabaseException, DuplicateEntryException {

       Message response;

        //check for duplicates
        try {
            List<Message> dupEntries = this.query(message, queryRequest, operators);
            if (dupEntries.size() > 0) {
                throw new DuplicateEntryException("Record already exists");
            }
        } catch (DatabaseException e) {
            throw new DatabaseException("Error inserting record");
        }

        Map<String, Object> services = message.getMap();
        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime dt = fmt.parseDateTime(message.getExpires());

        Date timestamp = dt.toDate();
        services.put("_timestamp", timestamp);

        Document doc = new Document();
        doc.putAll(services);

        try{
            coll.insertOne(doc);
        }catch(MongoWriteException mwe){
            throw new DatabaseException(mwe.getMessage());
        }catch(MongoWriteConcernException mwce){
            throw new DatabaseException("Error inserting record. Database exception due to write concerns: "+mwce.getMessage());
        } catch (MongoCommandException mce){
            throw new DatabaseException("Error inserting record. Database command execution failure: "+mce.getMessage());
        } catch(MongoException me){
            throw new DatabaseException("Error inserting record. Database exception:"+me.getMessage());
        }

        response = toMessage(doc);
        return response;

    }

    /**
     * This method updates an existing record in the database.
     *
     * @param serviceid The unique service identifier
     * @param updateRequest The fields to be modified
     * @return              The record that was modified (after modification) as a Message
     * @throws DatabaseException
     */
    public Message updateService(String serviceid, Message updateRequest) throws DatabaseException {

        Message response = new Message();

        if (serviceid != null && !serviceid.isEmpty()) {

            Document query = new Document();
            query.put(ReservedKeys.RECORD_URI, serviceid);

            Document updateObject = new Document("$set",new Document(updateRequest.getMap()));
            try {
                FindOneAndUpdateOptions updateOptions = new FindOneAndUpdateOptions();
                updateOptions.returnDocument(ReturnDocument.AFTER);

                Document result = (Document)coll.findOneAndUpdate(query, updateObject, updateOptions);

                if (result != null) {
                     result.remove("_timestamp");
                     result.remove("_id");
                    response = new Message(result);
                } else {
                    throw new DatabaseException("Error renewing record");
                }
            } catch (MongoException e) {
                throw new DatabaseException(e.getMessage());
            }
        } else {
            throw new DatabaseException("Record URI not specified!!!");
        }

        return response;

    }



    public List<Message> query(Message message, Message queryRequest, Message operators) throws DatabaseException {

        return this.query(message, queryRequest, operators, 0, 0);

    }

    public List<Message> query(Message message, Message queryRequest, Message operators, int maxResults, int skip) throws DatabaseException {

        Document query;

        if(queryRequest.getMap().isEmpty()){
            query = new Document();
        }else{
            query = buildQuery(queryRequest, operators);
        }


        ArrayList<Message> result = new ArrayList<Message>();

        try {
            FindIterable resultIterator = coll.find(query);
            MongoCursor cursor = resultIterator.iterator();

            while (cursor.hasNext()) {
                Document tmp = (Document)cursor.next();
                if(tmp.containsKey("_id")){
                    tmp.remove("_id");
                }
                if(tmp.containsKey("_timestamp")){
                    tmp.remove("_timestamp");
                }
                Message dbObjectMessage = new Message(tmp);
                result.add(dbObjectMessage);
                tmp=null;
            }

        } catch (MongoException e) {

            throw new DatabaseException("Error retrieving results");

        }

        return result;
    }


    public List<Message> queryAll() throws DatabaseException {

        Message msg = new Message();
        return query(msg, msg, msg);

    }

    /**
     * Builds the MongoDB query from the given map. The map is received from the REST API. All the operators
     * and the wildcard characters have to be converted to Mongo's wildcard characters and operators.
     *
     * @param queryRequest  The map containing the query key-values
     * @param operators     The map containing operators
     * @return              Mongo Document object containing the query
     */
    public Document buildQuery(Message queryRequest, Message operators) {

        Map<String, Object> queryMap = queryRequest.getMap();
        Map<String, String> opsMap = operators.getMap();

        List<HashMap<String, Object>> keyValueList = new ArrayList<HashMap<String, Object>>();

        for (String queryKey:queryMap.keySet()) {

            HashMap<String, Object> tmpHash = new HashMap<String, Object>();
            Object queryValue = queryMap.get(queryKey);

            if (queryValue instanceof String) {

                String val = (String) queryValue;
                //deal with metacharacter
                if (val.contains("*")) {
                    Pattern value = getMongoWildCardPattern(val);
                    tmpHash.put(queryKey,value);
                } else {
                    tmpHash.put(queryKey, (String) queryValue);
                }

            } else if (queryValue instanceof List) {

                List<Object> values = (List<Object>) queryValue;
                ArrayList newValues = new ArrayList();

                if (values.size() > 1) {

                    for (int i = 0; i < values.size(); i++) {
                        String val = (String) values.get(i);
                        if (val.contains("*")) {
                            Pattern value = getMongoWildCardPattern(val);
                            newValues.add(value);
                        } else {
                            newValues.add(val);
                        }
                    }
                    tmpHash.put(queryKey,newValues);

                    HashMap<String, Object> listvalues = new HashMap<String, Object>();

                    if (opsMap.containsKey(queryKey) && listOperatorMapping.containsKey(opsMap.get(queryKey).toLowerCase())) {
                        //get the operator
                        String curop = listOperatorMapping.get(opsMap.get(queryKey).toLowerCase());
                        listvalues.put(curop, newValues);
                        tmpHash.put(queryKey, listvalues);
                    } else {
                        tmpHash.put(queryKey, newValues);
                    }

                } else if (values.size() == 1) {

                    String val = (String) values.get(0);
                    if (val.contains("*")) {
                        Pattern value = getMongoWildCardPattern(val);
                        tmpHash.put(queryKey,value);
                    } else {
                        tmpHash.put(queryKey, values.get(0));
                    }

                }

            }

            if (!tmpHash.isEmpty()) {
                keyValueList.add(tmpHash);
            }

        }

        Document query = new Document();
        String queryOp = operators.getOperator();
        String op;

        if (queryOp == null || queryOp.isEmpty()) {
            op = ReservedValues.RECORD_OPERATOR_DEFAULT;        //uses only the first value from the list
        }else {
            op=queryOp.toLowerCase();
        }

        String mongoOp = "";
        if (operatorMapping.containsKey(op)) {
            mongoOp = operatorMapping.get(op);
        }

        if (!keyValueList.isEmpty()) {
            query.put(mongoOp, keyValueList);
        }

        return query;

    }


    /**
     * This is a private method that retrieves the mongo wild card pattern. The REST API of sLS supports only prefix and suffix matching
     * using the '*' character. Whereas, mongo uses PCRE standards - '^' and '$'. This method maps prefix and suffix
     * received from the REST API to the Mongo wild card operation
     *
     * @param stringPattern The string pattern received from the REST API
     * @return              Pattern object containing the Mongo's PCRE
     */
    private Pattern getMongoWildCardPattern(String stringPattern) {

        Pattern newPattern = null;

        if (stringPattern.endsWith("*")) {
            newPattern = Pattern.compile("^" + stringPattern.substring(0, stringPattern.length() - 1));
        } else if (stringPattern.startsWith("*")) {
            newPattern = Pattern.compile(stringPattern.substring(1, stringPattern.length()) + "$");
        }

        return newPattern;
    }


    /**
     * This method retrieves the record from the database using the record uri
     *
     * @param recorduri the uri of the record
     * @return Entire record as a Message object
     * @throws DatabaseException
     */
    public Message getRecordByURI(String recorduri) throws DatabaseException {

        Document query = new Document();
        query.put(ReservedKeys.RECORD_URI, recorduri);
        Message result = null;

        try {

            FindIterable resultIterator = coll.find(query);
            MongoCursor cursor = resultIterator.iterator();

            //must have only one record. In case there are multiple records, the API can return only one record so get the first one
            if (cursor.hasNext()) {
                result = toMessage((Document) cursor.next());
            }

        } catch (MongoException e) {

            throw new DatabaseException(e.getMessage());

        }

        return result;
    }

    /**
     * Inserts the given record into the database
     *
     * @param message The record to be added to the database
     * @throws DatabaseException
     */

    public void publishService(Message message) throws DatabaseException {

        try {

            Document document = new Document(message.getMap());

            coll.insertOne(document);
        } catch (MongoException e) {

            throw new DatabaseException(e.getMessage());

        }

    }

    /**
     * Returns the number of records in the database
     *
     * @return number of records in the database
     * @throws DatabaseException
     */

    public long getCount() throws DatabaseException {

        try {
            return coll.count();
        } catch (MongoException e) {
            throw new DatabaseException(e.getMessage());
        }


    }

    /**
     *  This method deletes the record for given uri
     *
     * @param recorduri
     * @return Message - returns the deleted record as a Message object
     * @throws DatabaseException
     */
    public Message deleteRecord(String recorduri) throws DatabaseException, RecordNotFoundException {

        Document query = new Document();
        query.put(ReservedKeys.RECORD_URI, recorduri);

        //retrieve the record to send it back in the response
        Message response = getRecordByURI(recorduri);

        if(response !=null && response.getMap() != null && !response.getMap().isEmpty()){
            try {

                DeleteResult deleteResult = coll.deleteOne(query);
                if (deleteResult.getDeletedCount() != 1) {
                    throw new DatabaseException("Error deleting record");
                }
            } catch (MongoException e) {
                throw new DatabaseException(e.getMessage());
            }

        }else{
            throw new RecordNotFoundException("Record not found in the database");
        }



        return response;

    }



    /**
     *  This method deletes expired records from db and returns number of records deleted.
     *
     * @param datetime All records that have _timestamp before "datetime" are deleted
     * @return         number of records deleted
     * @throws DatabaseException
     */
    public long deleteExpiredRecords(Date datetime) throws DatabaseException {

        try {
            DeleteResult result = coll.deleteMany(lte("_timestamp", datetime));
            return result.getDeletedCount();
        } catch (MongoException e) {

            throw new DatabaseException(e.getMessage());

        }


    }


    /**
     * This method deletes all the content in the DB and returns number of records deleted.
     *
     * @return  long - number of records deleted
     * @throws DatabaseException
     */
    public long deleteAllRecords() throws DatabaseException {

        try {

            DeleteResult deleteResult = coll.deleteMany(new Document());
            return deleteResult.getDeletedCount();
        } catch (MongoException e) {

            throw new DatabaseException(e.getMessage());

        }

    }

    private Message toMessage(Document doc){
        Message result;
        if(doc != null){
            doc.remove("_timestamp");
            doc.remove("_id");
            result = new Message(doc);
        }else{
            result = new Message();
        }
        return result;
    }

    //Bulk API
    public List<Message> bulkInsert() throws DatabaseException {

        return null;

    }

    public List<Message> bulkDelete() throws DatabaseException {

        return null;


    }


}