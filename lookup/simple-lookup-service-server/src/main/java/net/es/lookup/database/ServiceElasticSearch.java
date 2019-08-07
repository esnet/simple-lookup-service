package net.es.lookup.database;

import com.google.gson.Gson;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.common.exception.internal.RecordNotFoundException;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.Document;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;

public class ServiceElasticSearch {

  // URl to connect to the databse
  private URI location;
  // Port-1 of the database
  private int port1;
  // Port-2 of the databse
  private int port2;
  // Name of the database
  private String indexName;

  private static Logger Log = LogManager.getLogger(ServiceElasticSearch.class);

  private RestHighLevelClient client;

  /**
   * default initialization for the database for testing on localhost
   *
   * @throws URISyntaxException for incorrect dburl
   */
  public ServiceElasticSearch() throws URISyntaxException {
    // Todo
    //    this.port1 = DatabaseConnectionKeys.DatabasePort1;
    //    this.port2 = DatabaseConnectionKeys.DatabasePort2;
    //    this.location = new URI(DatabaseConnectionKeys.server);
    //    this.indexName = DatabaseConnectionKeys.DatabaseName;
    init();
  }

  /**
   * @param dburl URl to connect to the database
   * @param dbport1 Port 1 of the Database
   * @param dbport2 Port 2 of the Database
   * @param dbname Name of the Database
   * @throws URISyntaxException for incorrect dburl
   */
  public ServiceElasticSearch(String dburl, int dbport1, int dbport2, String dbname)
      throws URISyntaxException {
    this.location = new URI(dburl);
    this.port1 = dbport1;
    this.port2 = dbport2;
    this.indexName = dbname;
    init();
  }

  /** initializes the database */
  private void init() {
    client =
        new RestHighLevelClient(
            RestClient.builder(
                new HttpHost(this.location.toString(), this.port1, "http"),
                new HttpHost(this.location.toString(), this.port2, "http")));
    GetRequest getRequest = new GetRequest(this.indexName, "1");
    getRequest.fetchSourceContext(new FetchSourceContext(false));
    getRequest.storedFields("_none_");
    // Checks if the current index exists and creates it if it doesn't
    try {
      client.get(getRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ElasticsearchStatusException e) {
      // In case index doesn't exist
      Log.info("Creating index");
      CreateIndexRequest create = new CreateIndexRequest(this.indexName.toLowerCase());
      try {
        client.indices().create(create, RequestOptions.DEFAULT);
      } catch (IOException ex) {
        Log.error("unable to create index!");
      }
    }
  }

  /**
   * Closes the connection to the group
   *
   * @throws IOException If there is an error closing the connection
   */
  public void closeConnection() throws IOException {
    client.close();
    client = null;
    System.gc();
  }

  /**
   * Inserts record into database. The method checks if a record exists before inserting it into the
   * database.
   *
   * @param message record to be added to the database
   * @return Message that was added to the database
   * @throws DuplicateEntryException Thrown if database already contains the record that is trying
   *     to be added
   * @throws IOException Error in get request to the database
   */
  public Message queryAndPublishService(Message message)
      throws DuplicateEntryException, IOException {
    Message queryRequest = new Message();
    queryRequest.add("uri", message.getURI());
    queryRequest.add("type", message.getRecordType());
    if(exists(message)){
      throw new DuplicateEntryException("Record already exists");
    } // checking if message already exists in the index
    Message timestampedMessage = addTimestamp(message); // adding a timestamp to the message
    insert(timestampedMessage, queryRequest); // inserting the timestamped message
    return toMessage(timestampedMessage); // return the message that was added to the index
  }

  public List<Message> bulkQueryAndPublishService(Queue<Message> messages)
          throws IOException {

    List<Message> failed = new ArrayList<>();
    BulkRequest request = new BulkRequest();
    Map<String, Message> messageMap = new HashMap<>();
    Gson gson = new Gson();

    for (Message message : messages) {
        if(exists(message)){
          message.setErrorMessage("");
          Map<String, Object> errorMessageMap = message.getMap();
          errorMessageMap.put("error", "Document already exists");
          Message errorMessage = new Message(errorMessageMap);
          failed.add(errorMessage);
        } else {
          Message timestampedMessage = addTimestamp(message);
          request.add(
              new IndexRequest(this.indexName)
                  .id(message.getURI())
                  .source(gson.toJson(timestampedMessage), XContentType.JSON));
          messageMap.put(message.getURI(), message);
        }
    }
    if (request.numberOfActions() != 0) {
      BulkResponse bulkResponse = client.bulk(request, RequestOptions.DEFAULT);
      if (bulkResponse.hasFailures()) {
        Iterator<BulkItemResponse> responses = bulkResponse.iterator();
        while (responses.hasNext()) {
          BulkItemResponse response = responses.next();
          if (response.isFailed()) {
            Map<String, Object> errorMessageMap = messageMap.get(response.getId()).getMap();
            errorMessageMap.put("error", response.getFailureMessage());
            Message errorMessage = new Message(errorMessageMap);
            failed.add(errorMessage);
          }
        }
      }
    }
    return failed; // todo return the message that was added to the index
  }

  /**
   * Deletes the record for a given URI
   *
   * @param recordURI URI of the record o be deleted
   * @return Message - returns the deleted record as a Message Object
   * @throws IOException If error deleting record
   */
  public Message deleteRecord(String recordURI) throws RecordNotFoundException, IOException {
    DeleteRequest request = new DeleteRequest(this.indexName, recordURI);
    Message existingRecord = getRecordByURI(recordURI);
    DeleteResponse deleteResponse = client.delete(request, RequestOptions.DEFAULT);
    if (deleteResponse.status().getStatus() != 200) {
      throw new RecordNotFoundException("Unable to find record");
    }
    return existingRecord;
  }

  /**
   * This method deletes all the records in a given Db and returns the number of records deleted.
   *
   * @return number of records deleted returns 0 if the database doesn't exist yet
   * @throws IOException Thrown if error deleting the records
   */
  public long deleteAllRecords() throws IOException {
    try {
      DeleteIndexRequest request = new DeleteIndexRequest(this.indexName);
      CountRequest countRequest = new CountRequest();
      SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
      searchSourceBuilder.query(QueryBuilders.matchAllQuery());
      countRequest.source(searchSourceBuilder);
      CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
      long count = countResponse.getCount();
      AcknowledgedResponse deleteIndexResponse =
          client.indices().delete(request, RequestOptions.DEFAULT);
      if (!deleteIndexResponse.isAcknowledged()) {
        throw new IOException();
      }
      return count;
    } catch (ElasticsearchStatusException e) {
      Log.error("Index doesn't exist");
      return 0;
    }
  }

  /**
   * Receives the record from the database using the record URI
   *
   * @param recordURI URI of the record needed to be returned
   * @return Entire record as a message object null if record doesn't exist
   * @throws IOException thrown if error accessing the record
   */
  public Message getRecordByURI(String recordURI) throws IOException {
    GetRequest getRequest = new GetRequest(this.indexName, recordURI);
    String[] includes = Strings.EMPTY_ARRAY;
    String[] excludes = Strings.EMPTY_ARRAY;
    FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
    getRequest.fetchSourceContext(fetchSourceContext);
    GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
    Map<String, Object> responseMap;
    responseMap = (Map<String, Object>) getResponse.getSourceAsMap();
    if(responseMap == null){
      return null;
    }

    return new Message(responseMap);
  }

  /**
   * This method updates a given request in the database
   *
   * @param serviceId The unique service identifier
   * @param updateRequest the fields to be modified
   * @return The record that was modified (after modification) as a Message
   * @throws IOException if error updating record
   */
  public Message updateService(String serviceId, Message updateRequest) throws IOException {
    try {
      if (serviceId != null && !serviceId.isEmpty()) {
        deleteRecord(serviceId); // Deletes previous record
        publishService(updateRequest); // Creates a new record with updated message
        return getRecordByURI(serviceId);
      } else {
        throw new IOException("Record URI not specified");
      }
    } catch (ElasticsearchStatusException | RecordNotFoundException e) {
      throw new IOException("Record URI does not exist");
    }
  }

  /**
   * Inserts the given record into the database
   *
   * @param message Record to be added to the database
   * @throws IOException Thrown if error writing to the database
   */
  public void publishService(Message message) throws IOException {
    Message queryRequest = new Message();
    queryRequest.add("uri", message.getURI());
    queryRequest.add("type", message.getRecordType());
    Message timestampedMessage = addTimestamp(message); // adding a timestamp to the message
    insert(timestampedMessage, queryRequest); // inserting the timestamped message
  }

  /**
   * this update handles updates to multiple records
   *
   * @param records Map of the uri and record to be updated
   * @return Message returns a message with the number of records updated
   * @throws IOException error is thrown if error updating database
   */
  public Message bulkUpdate(Map<String, Message> records) throws IOException {
    BulkRequest bulkRequest = new BulkRequest();
    Gson gson = new Gson();
    int count = 0;
    for (String URI : records.keySet()) {
      Message timeStampedMessage = addTimestamp(records.get(URI));
      String updateString = gson.toJson(timeStampedMessage);
      bulkRequest.add(new UpdateRequest(this.indexName, URI).doc(updateString, XContentType.JSON));
      count++;
    }
    BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    if (bulkResponse.hasFailures()) {
      throw new IOException("Error updating records");
    }
    Message response = new Message();
    response.add("renewed", count);
    return response;
  }

  /**
   * This method deletes expired records from the DB and returns the number of docs deleted
   *
   * @param dateTime All records that have _timestamp before "datetime" are deleted
   * @return number of all records deleted
   * @throws IOException thrown if error deleting records from database
   */
  public long deleteExpiredRecords(DateTime dateTime) throws IOException {

    RangeQueryBuilder rangeQueryBuilder =
        new RangeQueryBuilder("_timestampadded").lte(dateTime);
    DeleteByQueryRequest request =
        new DeleteByQueryRequest(this.indexName).setQuery(rangeQueryBuilder);
    BulkByScrollResponse bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
    return bulkResponse.getDeleted();
  }

  public List<Message> findRecordsInTimeRange(DateTime start, DateTime end) throws IOException {

    List<Message> result = new ArrayList<Message>();

    SearchRequest searchRequest = new SearchRequest(this.indexName);

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.rangeQuery("_lastUpdated").gt(start));
    searchSourceBuilder.query(QueryBuilders.rangeQuery("_lastUpdated").lte(end));
    searchRequest.source(searchSourceBuilder);

    SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    SearchHit[] searchHits = searchResponse.getHits().getHits();

    for (SearchHit search : searchHits) {
      // Get result as a map
      Map<String, Object> searchMap = search.getSourceAsMap();
      result.add(new Message(searchMap));
    }
    return result;
  }

  /**
   * Method to query records from database.
   * // Todo fix documentation
   * @param message original query request
   * @param queryRequest query keywords extracted from the priginal request
   * @param operators operators like ANY, ALL that specifies how query keywords should be applied
   * @param maxResults max results to be returned. not implemented
   * @return List of all the records
   */
  public List<Message> query(
      Message message, Message queryRequest, Message operators, int maxResults)
      throws DatabaseException {
    String operator = (String) operators.getMap().get("operator");

    return allQuery(queryRequest.getMap(), maxResults, operator);
  }

  private List<Message> allQuery(Map queryRequest, int maxResults, String operator) {

    SearchRequest searchRequest = new SearchRequest(this.indexName);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    if (maxResults != 0) {
      searchSourceBuilder.size(maxResults);
    }
    BoolQueryBuilder builder = QueryBuilders.boolQuery();
    List<Message> result = new ArrayList<>();
    if (operator.equalsIgnoreCase("all")) {
      for (Object key : queryRequest.keySet()) {
        String keyAsString = (String) key;
        if (!queryRequest.get(keyAsString).toString().contains("*")) {
          builder.must(matchQuery(keyAsString, queryRequest.get(keyAsString)));
        }else {
          builder.must(regexpQuery(keyAsString, (String) queryRequest.get(keyAsString)));
        }
      }
    } else {
      for (Object key : queryRequest.keySet()) {
        String keyAsString = (String) key;
        if (!queryRequest.get(keyAsString).toString().contains("*")) {
          builder.should(matchQuery(keyAsString, queryRequest.get(keyAsString)));
        }else {
          builder.should(regexpQuery(keyAsString, (String) queryRequest.get(keyAsString)));
        }      }
    }
    searchSourceBuilder.query(builder);
    try {

      // Getting all records from the database
      searchRequest.source(searchSourceBuilder);
      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      SearchHit[] searchHits = searchResponse.getHits().getHits();
      for (SearchHit hit : searchHits) {
        result.add(new Message((Map<String, Object>) hit.getSourceAsMap()));
      }
    } catch (ElasticsearchStatusException e) {
      Log.info("Couldn't find record in database" + e.getMessage());
    } catch (IOException e) {
      Log.error("Internal server error" + e.getMessage());
      throw new InternalServerErrorException(e.getMessage());
    }
    return result;
  }

  /**
   * Checks if the document already exists in the database
   *
   * @param queryRequest document to check existence of
   * @throws IOException Error searching the database
   * @throws DuplicateEntryException If there is a duplicate entry in the database
   */
  private boolean exists(Message queryRequest) throws IOException {

    // Getting the document to search for in map form
    Map<String, Object> queryMap = new TreeMap<>(queryRequest.getMap());

    // Removing objects that are not user generated
    queryMap.remove("expires");
    queryMap.remove("_lastUpdated");
    queryMap.remove("ttl");
    queryMap.remove("_timestampadded");
    queryMap.remove("test-id");
    queryMap.remove("uri");

    SearchRequest searchRequest = new SearchRequest(this.indexName);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    BoolQueryBuilder builder = QueryBuilders.boolQuery();
    // Creating query for all common items
    for (String key : queryMap.keySet()) {

      builder.must(matchQuery(key, removeBracketFrom(queryMap.get(key).toString())));
    }

    searchSourceBuilder.query(builder);
    try {

      // Getting all records from the database
      searchRequest.source(searchSourceBuilder);
      SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      SearchHit[] searchHits = searchResponse.getHits().getHits();

      if (searchHits.length > 0) {
        for (SearchHit hit : searchHits) {
          Map keyValuesMap = hit.getSourceAsMap();
          //Map keyValuesMap = (Map) hitMap.get("keyValues");
          keyValuesMap.remove("expires");
          keyValuesMap.remove("_lastUpdated");
          keyValuesMap.remove("ttl");
          keyValuesMap.remove("_timestampadded");
          keyValuesMap.remove("test-id");
          keyValuesMap.remove("uri");
          if (keyValuesMap.size() == queryMap.size()) {
            return true;
            //throw new DuplicateEntryException("Record already exists");
          }
        }
      }
    } catch (ElasticsearchStatusException e) {
      Log.info("Couldn't find record in database" + e.getMessage());
    }
    return false;
  }

  /**
   * Removes Square brackets from the start and end of a string eg: ["Hello"] -> "Hello"
   *
   * @param toRemove Object that can be converted to a string for which we want to remove brackets
   * @return String with brackets removed
   */
  private String removeBracketFrom(String toRemove) {
    String type = toRemove;
    if (type.startsWith("[") && type.endsWith("]")) {
      type = type.substring(type.indexOf("[") + 1, type.indexOf("]"));
    }
    return type;
  }

  /**
   * Inserts message into database
   *
   * @param message message to be inserted into database
   * @param queryRequest message with the URI of where the message is to be added
   * @throws IOException if insertion of message is unsuccessful
   */
  private void insert(Message message, Message queryRequest) throws IOException {
    IndexRequest request = new IndexRequest(this.indexName);
    request.id(queryRequest.getURI());
    Gson gson = new Gson();
    String json = gson.toJson(message.getMap());
    request.source(json, XContentType.JSON);
    client.index(request, RequestOptions.DEFAULT);
  }

  /**
   * Adds a timestamp and a lastupdated field to a given message
   *
   * @param message message which timestamp is to be added to
   * @return message with the timestamp and lastupdated field
   */
  private Message addTimestamp(Message message) {

    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    DateTime dt = fmt.parseDateTime(message.getExpires());

    Date timestamp = dt.toDate();
    message.add("_timestampadded", timestamp);
    message.add("_lastUpdated", new Date());
    return message;
  }

  private Message toMessage(Message message) {
    Map<String, Object> messageMap = message.getMap();
    if (messageMap != null) {
      messageMap.remove("_timestamp");
      messageMap.remove("_id");
      messageMap.remove("_lastUpdated");
      return new Message(messageMap);
    } else {
      return new Message();
    }
  }
}
