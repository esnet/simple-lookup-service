package net.es.lookup.integration;

import net.es.lookup.api.QueryServices;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceDaoMongoDb;
import net.es.lookup.protocol.json.JSONParser;
import net.es.lookup.records.Record;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/** Author: sowmya Date: 3/10/16 Time: 4:13 PM */
public class QueryServiceIT {

  private static ServiceDaoMongoDb database;
  private static LeaseManager leaseManager;
  private static String host = "localhost";
  private static int port = 27017;
  private static String dbname = "querytest";
  private static String collection = "records";

  @BeforeClass
  public static void connectsToDatabase() {

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

  @Test
  public void checksQuery() {

    System.out.println("Checks default query");
    try {
      database.deleteAllRecords();
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
      QueryServices queryServices = new QueryServices();
      String jsonResult = queryServices.query(new Message(), 0);
      List<Record> records = JSONParser.toRecords(jsonResult);
      assertEquals(records.size(), count);
    } catch (DatabaseException e) {
      fail("Database Exception: " + e.getMessage());
    } catch (ParserException e) {
      fail("JSON Parser Exception: " + e.getMessage());
    }

    System.out.println("Query a record - \tPASS\t");
  }

  @Test
  public void checksWildCardQuery() {

    System.out.println("Checks wildcard in query");
    try {
      database.deleteAllRecords();

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-organization", "ESnet");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-organization", "LBL");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

    } catch (DatabaseException e) {
      fail("Database Exception: " + e.getMessage());
    }

    try {
      long count = 50;
      QueryServices queryServices = new QueryServices();
      Message query = new Message();
      query.add("test-organization", "ES*");
      String jsonResult = queryServices.query(query, 0);
      List<Record> records = JSONParser.toRecords(jsonResult);
      assertEquals(count, records.size());
    } catch (ParserException e) {
      fail("JSON Parser Exception: " + e.getMessage());
    }

    System.out.println("Query using wildcard - \tPASS\t");
  }

  @Test
  public void checksAndOperator() {

    System.out.println("Checks and operator in query");
    try {
      database.deleteAllRecords();

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-organization", "ESnet");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-name", "bwctl");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

    } catch (DatabaseException e) {
      fail("Database Exception: " + e.getMessage());
    }

    try {
      long count = 0;
      QueryServices queryServices = new QueryServices();
      Message query = new Message();
      query.add("test-organization", "ESnet");
      query.add("test-name", "bwctl");
      query.add("operator", "ALL");
      String jsonResult = queryServices.query(query, 0);
      List<Record> records = JSONParser.toRecords(jsonResult);
      assertEquals(count, records.size());
    } catch (ParserException e) {
      fail("JSON Parser Exception: " + e.getMessage());
    }

    System.out.println("Query using wildcard - \tPASS\t");
  }

  @Test
  public void checksOrOperator() {

    System.out.println("Checks or operator in query");
    try {
      database.deleteAllRecords();

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-organization", "ESnet");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-name", "bwctl");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

    } catch (DatabaseException e) {
      fail("Database Exception: " + e.getMessage());
    }

    try {
      long count = 100;
      QueryServices queryServices = new QueryServices();
      Message query = new Message();
      query.add("test-organization", "ESnet");
      query.add("test-name", "bwctl");
      query.add("operator", "ANY");
      String jsonResult = queryServices.query(query, 0);
      List<Record> records = JSONParser.toRecords(jsonResult);
      assertEquals(count, records.size());
    } catch (ParserException e) {
      fail("JSON Parser Exception: " + e.getMessage());
    }

    System.out.println("Query using wildcard - \tPASS\t");
  }

  @Test
  public void checksOrOperatorInValues() {

    System.out.println("Checks or operator for list of values");
    try {
      database.deleteAllRecords();

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-organization", "ESnet");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

      for (int i = 0; i < 50; i++) {
        Message message = new Message();
        message.add("type", "test");
        message.add("test-organization", "LBL");

        String uuid = UUID.randomUUID().toString();
        message.add("uri", uuid);

        leaseManager.requestLease(message);

        database.publishService(message);
      }

    } catch (DatabaseException e) {
      fail("Database Exception: " + e.getMessage());
    }

    try {
      long count = 100;
      QueryServices queryServices = new QueryServices();
      Message query = new Message();
      List<String> values = new ArrayList<String>();
      values.add("ESnet");
      values.add("LBL");
      query.add("test-organization", values);
      query.add("test-organization-operator", "ANY");
      String jsonResult = queryServices.query(query, 0);
      List<Record> records = JSONParser.toRecords(jsonResult);
      assertEquals(count, records.size());
    } catch (ParserException e) {
      fail("JSON Parser Exception: " + e.getMessage());
    }

    System.out.println("Query using wildcard - \tPASS\t");
  }
}
