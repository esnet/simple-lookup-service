package net.es.lookup.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.common.exception.internal.RecordNotFoundException;
import net.es.lookup.database.ServiceDaoMongoDb;
import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/** Author: sowmya Date: 2/3/16 Time: 11:00 AM */
public class DatabaseIT {

  private static ServiceDaoMongoDb database;
  private static LeaseManager leaseManager;

  @BeforeClass
  public static void connectsToDatabase() {

    String host = "localhost";
    int port = 27017;
    String dbname = "test";
    String collection = "records";

    try {
      if (ServiceDaoMongoDb.getInstance() != null) {
        database = ServiceDaoMongoDb.getInstance();

      } else {
        database = new ServiceDaoMongoDb(host, port, dbname, collection);
      }

    } catch (DatabaseException e) {
      e.printStackTrace();
    }

    leaseManager = LeaseManager.getInstance();
  }

  @Before
  public void clearDatabase() {
    try {
      database.deleteAllRecords();
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }
    System.out.println("Cleared database");
  }

  @Test
  public void publishData() {

    try {
      Message message = new Message();
      message.add("type", "test");

      String uuid = UUID.randomUUID().toString();
      message.add("uri", uuid);

      leaseManager.requestLease(message);

      database.publishService(message);

    } catch (DatabaseException e) {
      fail("Database Exception: " + e.getMessage());
    }

    try {
      long count = database.getCount();
      assertEquals(1, count);
    } catch (DatabaseException e) {
      fail("Database Exception: " + e.getMessage());
    }

    System.out.println("Publish a record without checking for Duplicates test - \tPASS\t");
  }

  @Test
  public void removesOneRecord() {

    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add("uri", uuid);

    message.add("ttl", "PT10M");

    leaseManager.requestLease(message);

    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    DateTime dt = fmt.parseDateTime(message.getExpires());

    Date timestamp = dt.toDate();
    message.add("_timestamp", timestamp);

    try {
      database.publishService(message);
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }

    try {
      Message response = database.deleteRecord(uuid);
      assertEquals(uuid, response.getURI());
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    } catch (RecordNotFoundException e) {
      fail("RecordNotFoundException exception: " + e.getMessage());
    }

    System.out.println("Remove record based on Record URI - \tPASS\t");
  }

  @Test
  public void retrievesRecordByUri() {

    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add("uri", uuid);

    message.add("ttl", "PT10M");

    leaseManager.requestLease(message);

    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    DateTime dt = fmt.parseDateTime(message.getExpires());

    Date timestamp = dt.toDate();
    message.add("_timestamp", timestamp);

    try {
      database.publishService(message);
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }

    try {
      Message response = database.getRecordByUri(uuid);
      assertEquals(uuid, response.getURI());
      assertEquals("test", response.getRecordType());
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }
  }

  @Test
  public void insertRecordsAfterDuplicateCheck() {

    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add("uri", uuid);

    message.add("test-id", String.valueOf(1));

    message.add("ttl", "PT10M");

    leaseManager.requestLease(message);

    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    DateTime dt = fmt.parseDateTime(message.getExpires());

    Date timestamp = dt.toDate();
    message.add("_timestamp", timestamp);

    try {
      database.queryAndPublishService(message, message, new Message());
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    } catch (DuplicateEntryException e) {
      fail("Invalid duplicate entry exception");
    }

    try {
      database.queryAndPublishService(message, message, new Message());
      fail("Duplicate Entry not detected");
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    } catch (DuplicateEntryException e) {
      assertTrue("Duplicate Entry detected correctly", true);
    }

    System.out.println("Query and Publish test - \tPASS\t");
  }

  @Test
  public void updatesRecord() {
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add("uri", uuid);

    message.add("test-id", String.valueOf(1));

    message.add("ttl", "PT10M");

    leaseManager.requestLease(message);

    DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
    DateTime dt = fmt.parseDateTime(message.getExpires());

    Date timestamp = dt.toDate();
    message.add("_timestamp", timestamp);

    try {
      database.queryAndPublishService(message, message, new Message());
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    } catch (DuplicateEntryException e) {
      fail("Invalid duplicate entry exception");
    }

    try {
      String uri = message.getURI();
      leaseManager.requestLease(message);
      Message response = database.updateService(uri, message);

      assertEquals(message.getURI(), response.getURI());
      assertEquals(message.getExpires(), response.getExpires());

    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }

    System.out.println("Update Record test - \tPASS\t");
  }

  @Test
  public void queriesForAllRecords() {

    for (int i = 0; i < 1000; i++) {
      Message message = new Message();
      message.add("type", "test");

      String uuid = UUID.randomUUID().toString();
      message.add("uri", uuid);

      message.add("ttl", "PT1M");

      leaseManager.requestLease(message);

      DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
      DateTime dt = fmt.parseDateTime(message.getExpires());

      Date timestamp = dt.toDate();
      message.add("_timestamp", timestamp);

      try {
        database.publishService(message);
        message = null;
      } catch (DatabaseException e) {
        fail("Database exception: " + e.getMessage());
      }
    }

    try {
      List<Message> results = database.queryAll();

      assertEquals(1000, results.size());
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }

    System.out.println("Query all Records Test - \tPASS\t");
  }

  @Test
  public void buildsComplexQueries() {

    System.out.println("Testing query builder......");
    Document mongoquery;
    String jsonquery;
    Message query, operator;
    List<String> valueList;

    // 1. Empty query
    System.out.println("1. Empty Query, empty operators");
    mongoquery = database.buildQuery(new Message(), new Message());
    jsonquery = "{ }";

    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 2a. Key-val strings, empty ops
    System.out.println("2a. Key-value strings, empty operators");
    query = new Message();
    query.add("key1", "val1");
    query.add("key2", "val2");

    mongoquery = database.buildQuery(query, new Message());
    jsonquery = "{ \"$and\" : [{ \"key1\" : \"val1\" }, { \"key2\" : \"val2\" }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 2b. List of values, empty ops
    System.out.println("2b. List of values, empty operators");
    query = new Message();
    valueList = new ArrayList<String>();
    valueList.add("val11");
    valueList.add("val12");
    query.add("key1", valueList);
    query.add("key2", "val2");

    mongoquery = database.buildQuery(query, new Message());
    jsonquery = "{ \"$and\" : [{ \"key1\" : [\"val11\", \"val12\"] }, { \"key2\" : \"val2\" }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 2c. List of values with one element, empty ops
    System.out.println("2c. List of values with one element, empty operators");
    query = new Message();
    valueList = new ArrayList<String>();
    valueList.add("val11");
    query.add("key1", valueList);
    query.add("key2", "val2");

    mongoquery = database.buildQuery(query, new Message());
    jsonquery = "{ \"$and\" : [{ \"key1\" : \"val11\" }, { \"key2\" : \"val2\" }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 3a. List of key-values, op - ALL
    System.out.println("3a. Key-values strings, Operator=ALL");
    query = new Message();
    query.add("key1", "val1");
    query.add("key2", "val2");

    operator = new Message();
    operator.add("operator", "ALL");

    mongoquery = database.buildQuery(query, new Message());
    jsonquery = "{ \"$and\" : [{ \"key1\" : \"val1\" }, { \"key2\" : \"val2\" }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 3b. List of key-values, op - ANY
    System.out.println("3b. Key-values strings, Operator=ANY");
    query = new Message();
    query.add("key1", "val1");
    query.add("key2", "val2");

    operator = new Message();
    operator.add("operator", "ANY");

    mongoquery = database.buildQuery(query, operator);
    jsonquery = "{ \"$or\" : [{ \"key1\" : \"val1\" }, { \"key2\" : \"val2\" }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 4a. List of key-values, op - ANY
    System.out.println("4a. List of values, match ANY in list");
    query = new Message();
    valueList = new ArrayList<String>();

    valueList.add("val11");
    valueList.add("val12");
    valueList.add("val13");
    query.add("key1", valueList);
    query.add("key2", "val2");

    operator = new Message();
    operator.add("key1", "ANY");

    mongoquery = database.buildQuery(query, operator);
    jsonquery =
        "{ \"$and\" : [{ \"key1\" : { \"$in\" : [\"val11\", \"val12\", \"val13\"] } }, { \"key2\" : \"val2\" }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 4b. List of key-values, op - ALL
    System.out.println("4b. List of values, match ALL in list ");
    query = new Message();
    valueList = new ArrayList<String>();

    valueList.add("val11");
    valueList.add("val12");
    valueList.add("val13");
    query.add("key1", valueList);
    query.add("key2", "val2");

    operator = new Message();
    operator.add("key1", "ALL");

    mongoquery = database.buildQuery(query, operator);
    jsonquery =
        "{ \"$and\" : [{ \"key1\" : { \"$all\" : [\"val11\", \"val12\", \"val13\"] } }, { \"key2\" : \"val2\" }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 5a. Wild-card operation -  prefix matching
    System.out.println("5a. Simple wild-card operation - prefix matching ");
    query = new Message();

    query.add("key1", "va*");

    mongoquery = database.buildQuery(query, operator);
    jsonquery = "{ \"$and\" : [{ \"key1\" : { \"$regex\" : \"^va\", \"$options\" : \"\" } }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 5b. Wild-card operation - suffix matching
    System.out.println("5b. Simple wild-card operation - suffix matching ");
    query = new Message();

    query.add("key1", "*l1");

    mongoquery = database.buildQuery(query, operator);
    jsonquery = "{ \"$and\" : [{ \"key1\" : { \"$regex\" : \"l1$\", \"$options\" : \"\" } }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 6a. Wild-card operation -  prefix matching - in a list
    System.out.println("6a. Simple wild-card operation - prefix matching in a list ");
    query = new Message();
    valueList = new ArrayList<String>();

    valueList.add("val11");
    valueList.add("val12");
    valueList.add("sa*");
    query.add("key1", valueList);

    mongoquery = database.buildQuery(query, operator);
    jsonquery =
        "{ \"$and\" : [{ \"key1\" : { \"$all\" : [\"val11\", \"val12\", { \"$regex\" : \"^sa\", \"$options\" : \"\" }] } }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    // 6b. Wild-card operation -  suffix matching - in a list
    System.out.println("6b. Simple wild-card operation - suffix matching in a list ");
    query = new Message();
    valueList = new ArrayList<String>();

    valueList.add("val11");
    valueList.add("val12");
    valueList.add("*sa");
    query.add("key1", valueList);

    mongoquery = database.buildQuery(query, operator);
    jsonquery =
        "{ \"$and\" : [{ \"key1\" : { \"$all\" : [\"val11\", \"val12\", { \"$regex\" : \"sa$\", \"$options\" : \"\" }] } }] }";
    System.out.print("Checking: Is " + jsonquery + " equal to " + mongoquery.toJson());
    assertTrue(jsonquery.equals(mongoquery.toJson()));
    System.out.println("\t-\tPASS");

    System.out.println("Building Complex Mongo Queries Test  - \tPASS\t");
  }

  @Test
  public void removesExpiredRecords() {

    for (int i = 0; i < 1000; i++) {
      Message message = new Message();
      message.add("type", "test");

      String uuid = UUID.randomUUID().toString();
      message.add("uri", uuid);

      message.add("ttl", "PT1M");

      leaseManager.requestLease(message);

      DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
      DateTime dt = fmt.parseDateTime(message.getExpires());

      Date timestamp = dt.toDate();
      message.add("_timestamp", timestamp);

      try {
        database.publishService(message);
      } catch (DatabaseException e) {
        fail("Database exception: " + e.getMessage());
      }
    }

    // wait for 2 minutes before deleting
    try {
      Thread.sleep(120000);
    } catch (InterruptedException e) {
      fail("Sleep interrupted: " + e.getMessage());
    }

    Date date = new Date();

    try {
      long count = database.deleteExpiredRecords(date);
      assertEquals(1000, count);
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }

    System.out.println("Remove Expired Records Test - \tPASS\t");
  }

  @Test
  public void queryRecordsInTimeRange() {

    for (int i = 0; i < 1000; i++) {
      Message message = new Message();
      message.add("type", "test");

      String uuid = UUID.randomUUID().toString();
      message.add("uri", uuid);

      message.add("ttl", "PT1M");

      leaseManager.requestLease(message);

      DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
      DateTime dt = fmt.parseDateTime(message.getExpires());

      Date timestamp = dt.toDate();
      message.add("_lastUpdated", timestamp);

      try {
        database.publishService(message);
      } catch (DatabaseException e) {
        fail("Database exception: " + e.getMessage());
      }
    }

    // wait for 2 minutes before deleting
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      fail("Sleep interrupted: " + e.getMessage());
    }

    Date date = new Date();
    Date oldDate = new Date(date.getTime() - 120000);

    try {
      List<Message> messages = database.findRecordsInTimeRange(oldDate, date);
      assertEquals(1000, messages.size());
    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }

    System.out.println("Queried for records in time range - \tPASS\t");
  }

  @Test
  public void bulkUpdates() {
    Map<String, Message> recordsToUpdate = new HashMap<>();

    for (int i = 0; i < 500; i++) {
      Message message = new Message();
      message.add("type", "test");

      String uuid = UUID.randomUUID().toString();
      message.add("uri", uuid);

      message.add("test-id", String.valueOf(1));

      message.add("ttl", "PT10M");

      leaseManager.requestLease(message);

      DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
      DateTime dt = fmt.parseDateTime(message.getExpires());

      Date timestamp = dt.toDate();
      message.add("_timestamp", timestamp);

      try {
        Message publishedMessage = database.queryAndPublishService(message, message, new Message());
        recordsToUpdate.put(message.getURI(), publishedMessage);
      } catch (DatabaseException e) {
        fail("Database exception: " + e.getMessage());
      } catch (DuplicateEntryException e) {
        fail("Invalid duplicate entry exception");
      }
    }

    for (Entry<String, Message> record : recordsToUpdate.entrySet()) {
      String key = record.getKey();
      Message tmpMessage = record.getValue();
      leaseManager.requestLease(tmpMessage);
      recordsToUpdate.put(key, tmpMessage);
    }
    Message response = new Message();
    try {
      response = database.bulkUpdate(recordsToUpdate);

    } catch (DatabaseException e) {
      fail("Database exception: " + e.getMessage());
    }
    System.out.println(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT);
    assertEquals(
        response.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT), recordsToUpdate.size());

    System.out.println("Bulk Update Record - \tPASS\t");
  }
}
