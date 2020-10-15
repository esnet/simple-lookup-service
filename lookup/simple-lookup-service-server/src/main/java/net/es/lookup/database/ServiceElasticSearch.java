package net.es.lookup.database;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.common.exception.internal.RecordNotFoundException;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.PutMappingRequest;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import javax.ws.rs.InternalServerErrorException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;


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
  private String indexMapping;

  private static Logger Log = LogManager.getLogger(ServiceElasticSearch.class);

  private RestHighLevelClient client = null;
  private static ServiceElasticSearch instance = null;

  private int DEFAULT_RESULTS_SIZE = 5000;

  public static ServiceElasticSearch getInstance() {

    return ServiceElasticSearch.instance;
  }

  private synchronized void init() throws DatabaseException {

    if (ServiceElasticSearch.instance != null) {

      // An instance has been already created.
      throw new DatabaseException("Attempt to create a second instance of ElasticSearch");
    }

    ServiceElasticSearch.instance = this;
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
      Log.error(e.getMessage());
      throw new DatabaseException(e.getMessage());
    } catch (ElasticsearchStatusException e) {
      // In case index doesn't exist
      Log.info("Creating index");
      CreateIndexRequest create = new CreateIndexRequest(this.indexName.toLowerCase());
      try {
        client.indices().create(create, RequestOptions.DEFAULT);
        if (indexMapping != null && !indexMapping.isEmpty()){
          PutMappingRequest mappingRequest = new PutMappingRequest(this.indexName.toLowerCase());
          mappingRequest.source(this.indexMapping, XContentType.JSON);
          client.indices().putMapping(mappingRequest, RequestOptions.DEFAULT);
        }

      } catch (IOException ex) {
        Log.error("unable to create index!");
        throw new DatabaseException(ex.getMessage());
      }
    }
  }

  /**
   * @param dburl URl to connect to the database
   * @param dbport1 Port 1 of the Database
   * @param dbport2 Port 2 of the Database
   * @param dbname Name of the Database
   * @throws URISyntaxException for incorrect dburl
   */
  public ServiceElasticSearch(String dburl, int dbport1, int dbport2, String dbname)
      throws URISyntaxException, DatabaseException {
    this.location = new URI(dburl);
    this.port1 = dbport1;
    this.port2 = dbport2;
    this.indexName = dbname;
    this.indexMapping = "";
    init();
  }

  public ServiceElasticSearch(String dburl, int dbport1, int dbport2, String dbname, String indexMapping)
      throws URISyntaxException, DatabaseException {
    this.location = new URI(dburl);
    this.port1 = dbport1;
    this.port2 = dbport2;
    this.indexName = dbname;
    this.indexMapping = indexMapping;
    init();
  }

  /**
   * Closes the connection to the group
   *
   * @throws IOException If there is an error closing the connection
   */
  public void closeConnection() throws IOException {
    // client.close();
  }

  /**
   * Inserts record into database. The method checks if a record exists before
   * inserting it into the database.
   *
   * @param message record to be added to the database
   * @return Message that was added to the database
   * @throws DuplicateEntryException Thrown if database already contains the
   *                                 record that is trying to be added
   * @throws DatabaseException
   */
  public Message queryAndPublishService(Message message, Message queryRequest, Message operators) throws DuplicateEntryException, DatabaseException {
    try {
      Log.debug("Running query for duplicates");
      for (Object key: operators.getMap().keySet()){
        Log.debug("Printing keys in register message"+ key.toString());
      }
      List<Message> dupEntries = this.query(message, queryRequest, operators);
      Log.debug("Checking for dups -"+ dupEntries.size() );
      if (dupEntries.size() > 0) {
        throw new DuplicateEntryException("Record already exists");
      }
    } catch (DatabaseException e) {
      throw new DatabaseException("Error inserting record");
    }
    Message timestampedMessage = addTimestamp(message); // adding a timestamp to the message
    insert(timestampedMessage); // inserting the timestamped message
    return removeLsAddedFields(timestampedMessage); // return the message that was added to the index
   }

  /**
   * Deletes the record for a given URI
   *
   * @param recordURI URI of the record o be deleted
   * @return Message - returns the deleted record as a Message Object
   * @throws DatabaseException
   */
  public Message deleteRecord(String recordURI) throws RecordNotFoundException, DatabaseException {
    DeleteRequest request = new DeleteRequest(this.indexName, recordURI);
    Message existingRecord = getRecordByURI(recordURI);
    DeleteResponse deleteResponse;
    try {
      deleteResponse = client.delete(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      Log.error("Caught Elastic IOException"+e.getMessage());
      throw new DatabaseException(e.getMessage());
    }
    if (deleteResponse.status().getStatus() != 200) {
      throw new RecordNotFoundException("Unable to find record");
    }
    return existingRecord;
  }

  /**
   * This method deletes all the records in a given Db and returns the number of
   * records deleted.
   *
   * @return number of records deleted returns 0 if the database doesn't exist yet
   * @throws DatabaseException
   */
  public long deleteAllRecords() throws DatabaseException {
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
    } catch (IOException e) {
      Log.error("Caught Elastic IOException"+e.getMessage());
      throw new DatabaseException(e.getMessage());
    }
  }

  /**
   * Receives the record from the database using the record URI
   *
   * @param recordURI URI of the record needed to be returned
   * @return Entire record as a message object null if record doesn't exist
   * @throws DatabaseException
   */
  public Message getRecordByURI(String recordURI) throws DatabaseException {
    GetRequest getRequest = new GetRequest(this.indexName, recordURI);
    String[] includes = Strings.EMPTY_ARRAY;
    String[] excludes = Strings.EMPTY_ARRAY;
    FetchSourceContext fetchSourceContext = new FetchSourceContext(true, includes, excludes);
    getRequest.fetchSourceContext(fetchSourceContext);
    GetResponse getResponse;
    try {
      getResponse = client.get(getRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
      Log.error("Caught Elastic IOException"+e.getMessage());
      throw new DatabaseException(e.getMessage());
    }
    Map<String, Object> responseMap;
    responseMap = (Map<String, Object>) getResponse.getSourceAsMap();
    if (responseMap == null) {
      return null;
    }

    Message responseAsMessage = removeLsAddedFields(new Message(responseMap));

    return responseAsMessage;
  }

  /**
   * This method updates a given request in the database
   *
   * @param serviceId     The unique service identifier
   * @param updateRequest the fields to be modified
   * @return The record that was modified (after modification) as a Message
   * @throws DatabaseException
   * @throws IOException       if error updating record
   */
  public Message updateService(String serviceId, Message updateRequest) throws DatabaseException {
    Message responseAsMessage = new Message();
    Message receivedRequest = addTimestamp(updateRequest);
    Log.debug("Inside updateService");
    try {
      Log.debug("Processing updateService: "+ serviceId);
      if (serviceId != null && !serviceId.isEmpty()) {
          Log.debug(receivedRequest.getMap().toString());
          UpdateRequest updateElasticRequest = new UpdateRequest(this.indexName, serviceId);
          updateElasticRequest.doc(receivedRequest.getMap());
          updateElasticRequest.fetchSource(true);
          UpdateResponse updateResponse = null;
		try {
			updateResponse = client.update(updateElasticRequest, RequestOptions.DEFAULT);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.error("Update failed"+ e.getMessage());
		}
          Log.debug("Updated Response: "+ updateResponse.getResult());
          if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
            GetResult result = updateResponse.getGetResult();
            if (result.isExists()) {
              Map<String, Object> sourceAsMap = result.sourceAsMap();
              if(sourceAsMap == null){
                return null;
              }
              responseAsMessage = removeLsAddedFields(new Message(sourceAsMap));
              Log.info("Completed updateService");
              return responseAsMessage;
            }else{
              Log.debug("Empty result"+result.getId());
            }
          }else{
            throw new DatabaseException("Update operation error in Elasticsearch"+ updateResponse.getResult());
          }
      } else {
        throw new DatabaseException("Record URI not specified");
      }
    } catch (ElasticsearchStatusException e) {
      throw new DatabaseException("Record URI does not exist"+e.getMessage());
    }
    return null;
  }

  /**
   * Inserts the given record into the database
   *
   * @param message Record to be added to the database
   * @throws DatabaseException
   */
  public void publishService(Message message) throws DatabaseException {
    Message timestampedMessage = addTimestamp(message); // adding a timestamp to the message
    insert(timestampedMessage); // inserting the timestamped message */
  }

  /**
   * this update handles updates to multiple records
   *
   * @param records Map of the uri and record to be updated
   * @return Message returns a message with the number of records updated
   * @throws DatabaseException
   */
  public Message bulkUpdate(Map<String, Message> records) throws DatabaseException {
    BulkRequest bulkRequest = new BulkRequest();
    int count = 0;
    for (String recordUri : records.keySet()) {
      Message timeStampedMessage = addTimestamp(records.get(recordUri));
      bulkRequest.add(new UpdateRequest(this.indexName, recordUri).doc(timeStampedMessage.getMap()));
      count++;
    }
    BulkResponse bulkResponse;
    try {
      bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
      Log.error("Caught Elastic IOException"+e.getMessage());
      throw new DatabaseException(e.getMessage());
    }
    if (bulkResponse.hasFailures()) {
      Log.error("Error updating records");
      throw new DatabaseException("Error updating records");
    }
    Message response = new Message();
    response.add("renewed", count);
    return response;
  }

  /**
   * This method deletes expired records from the DB and returns the number of
   * docs deleted
   *
   * @param dateTime All records that have _timestamp before "datetime" are
   *                 deleted
   * @return number of all records deleted
   * @throws DatabaseException thrown if error deleting records from database
   */
  public long deleteExpiredRecords(DateTime dateTime) throws DatabaseException {
    findRecordsInTimeRange(new DateTime(0), dateTime);
    RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder("_expiresAsTimestamp").lte(dateTime.getMillis());
    DeleteByQueryRequest request =
        new DeleteByQueryRequest(this.indexName).setQuery(rangeQueryBuilder);
    BulkByScrollResponse bulkResponse;
    try {
      bulkResponse = client.deleteByQuery(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      Log.error("Caught Elastic IOException"+e.getMessage());
      throw new DatabaseException(e.getMessage());
    }
    return bulkResponse.getDeleted();
  }

  public List<Message> findRecordsInTimeRange(DateTime start, DateTime end) throws DatabaseException {

    List<Message> result = new ArrayList<Message>();

    SearchRequest searchRequest = new SearchRequest(this.indexName);

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(QueryBuilders.rangeQuery("_lastUpdated").gt(start));
    searchSourceBuilder.query(QueryBuilders.rangeQuery("_lastUpdated").lte(end));
    searchRequest.source(searchSourceBuilder);

    SearchResponse searchResponse;
    try {
      searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
    } catch (IOException e) {
      Log.error("Caught Elastic IOException"+e.getMessage());
      throw new DatabaseException(e.getMessage());
    }
    SearchHit[] searchHits = searchResponse.getHits().getHits();

    for (SearchHit search : searchHits) {
      // Get result as a map
      Map<String, Object> searchMap = search.getSourceAsMap();
      result.add(new Message(searchMap));
    }
    return result;
  }

  public List<Message> query(Message message, Message queryRequest, Message operators)
      throws DatabaseException {

    return this.query(message, queryRequest, operators, DEFAULT_RESULTS_SIZE);
  }

  /**
   * Method to query records from database. // Todo fix documentation
   *
   * @param message original query request
   * @param queryRequest query keywords extracted from the original request
   * @param operators operators like ANY, ALL that specifies how query keywords should be applied
   * @param maxResults max results to be returned. not implemented
   * @return List of all the records
   */
  public synchronized List<Message> query(
      Message message, Message queryRequest, Message operators, int maxResults)
      throws DatabaseException {
    String operator = (String) operators.getMap().get("operator");

    List<Message> finalSearchResults = new ArrayList<>();
    SearchRequest searchRequest = buildElasticSearchRequest(queryRequest.getMap(), maxResults, operator);
    Log.debug("Inside query: "+searchRequest.toString());
    SearchResponse searchResponse = null;

    try {
      searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
      String scrollId = searchResponse.getScrollId();
      SearchHits hits = searchResponse.getHits();
      finalSearchResults.addAll(processSearchResponse(hits));

      ClearScrollRequest clearScrollRequest =
          new ClearScrollRequest(); // to eventually clean up the scrolls

      do {
        SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId);
        scrollRequest.scroll(TimeValue.timeValueSeconds(10));
        SearchResponse searchScrollResponse = client.scroll(scrollRequest, RequestOptions.DEFAULT);
        scrollId = searchScrollResponse.getScrollId();
        hits = searchScrollResponse.getHits();
        if (hits.getHits().length > 0) {
          finalSearchResults.addAll(processSearchResponse(hits));
        }
        clearScrollRequest.addScrollId(scrollId);
      } while (hits.getHits().length != 0);
      client.clearScroll(clearScrollRequest, RequestOptions.DEFAULT); //clearing the scrolls
    } catch (IOException e) {
      Log.error("Internal server error" + e.getMessage());
      throw new InternalServerErrorException(e.getMessage());
    }
    return finalSearchResults;
  }

  private synchronized SearchRequest buildElasticSearchRequest(Map queryRequest, int maxResults, String operator) {
    
    Log.info("Inside buildElasticSearchRequest method");
    SearchRequest searchRequest = new SearchRequest(this.indexName);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    if (maxResults != 0) {
      searchSourceBuilder.size(maxResults);
    }else{
      searchSourceBuilder.size(DEFAULT_RESULTS_SIZE);
    }
    BoolQueryBuilder boolKeywordQueryBuilder = QueryBuilders.boolQuery();
    if (operator.equalsIgnoreCase("all")) {
      Log.info("Inside buildElasticSearchRequest ALL case");
      for (Object key : queryRequest.keySet()) {
        String keyAsString = (String) key;
        Object value = queryRequest.get(keyAsString);
        if (value instanceof String){
          String valueString = (String) value;
          if (valueString.contains("*")) {
            String regexString = processWildCardPattern(valueString);
            boolKeywordQueryBuilder.must(regexpQuery(keyAsString, regexString));
            
          } else {
            boolKeywordQueryBuilder.must(matchQuery(keyAsString, valueString));
          }
        }else if(value instanceof List){
          Log.debug("Query values are list - ALL case");
          for (Object eachVal: (List)value ){
            String eachValString = (String) eachVal;
            if (eachValString.contains("*")) {
              String regexString = processWildCardPattern(eachValString);
              boolKeywordQueryBuilder.must(regexpQuery(keyAsString, eachValString));
            } else {
              
              boolKeywordQueryBuilder.must(matchQuery(keyAsString, eachValString));
            }
          }
        }
        
      }
    } else {
      Log.debug("Inside buildElasticSearchRequest ANY case");
      for (Object key : queryRequest.keySet()) {
        String keyAsString = (String) key;
        Object value = queryRequest.get(keyAsString);
        if (value instanceof String){
          String valueString = (String) value;
          if (valueString.contains("*")) {
            String regexString = processWildCardPattern(valueString);
            boolKeywordQueryBuilder.should(regexpQuery(keyAsString, regexString));
          } else {
            boolKeywordQueryBuilder.should(matchQuery(keyAsString, valueString));
          }
        }else if(value instanceof List){
          for (Object eachVal: (List) value ){
            String eachValString = (String) eachVal;
            if (eachValString.contains("*")) {
              String regexString = processWildCardPattern(eachValString);
              boolKeywordQueryBuilder.should(regexpQuery(keyAsString, regexString));
            } else {
              boolKeywordQueryBuilder.should(matchQuery(keyAsString, eachValString));
            }
          }
        }
        
      }

    }
    Log.debug(searchSourceBuilder.toString());
    searchSourceBuilder.query(boolKeywordQueryBuilder);

    searchRequest.source(searchSourceBuilder);
    searchRequest.scroll(TimeValue.timeValueSeconds(60));
    return searchRequest;
  }

private String processWildCardPattern(String searchTerm){
  if(searchTerm == null || searchTerm.length()<1){
    return searchTerm;
  }
  String regexpSearchTerm = searchTerm;
  if (searchTerm.contains("*")){

     regexpSearchTerm = searchTerm.toLowerCase().replace("*",".*");
  }
  return regexpSearchTerm;
}

  private List<Message> processSearchResponse(SearchHits searchHits) {
    List<Message> result = new ArrayList<>();
    for (SearchHit hit : searchHits.getHits()) {
      Message tmpResult = new Message((Map<String, Object>) hit.getSourceAsMap());
      if (tmpResult != null && tmpResult.getMap() != null) {
        result.add(tmpResult);
      }
    }
    return result;
  }

  /**
   * Inserts message into database
   *
   * @param message      message to be inserted into database
   * @param queryRequest message with the URI of where the message is to be added
   * @throws DatabaseException if insertion is unsuccessful
   */
  private void insert(Message message) throws DatabaseException {
    IndexRequest request = new IndexRequest(this.indexName);
    request.id(message.getURI());
    Gson gson = new Gson();
    String json = gson.toJson(message.getMap());
    request.source(json, XContentType.JSON);
    try {
      client.index(request, RequestOptions.DEFAULT);
    } catch (IOException e) {
      Log.error("Throwing DatabaseException"+ e.getMessage());
      throw new DatabaseException(e.getMessage());
    }
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
    message.add("_expiresAsTimestamp", timestamp.getTime());
    message.add("_lastUpdated", new Date());
    return message;
  }

  private Message removeLsAddedFields(Message message) {
    Map<String, Object> messageMap = message.getMap();
    if (messageMap != null) {
      messageMap.remove("_expiresAsTimestamp");
      messageMap.remove("_id");
      messageMap.remove("_lastUpdated");
      return new Message(messageMap);
    } else {
      return new Message();
    }
  }
}
