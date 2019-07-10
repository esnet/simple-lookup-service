package net.es.lookup.database;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.common.exception.internal.RecordNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class ServiceElasticSearchTest {

  private ServiceElasticSearch client;
  private static Logger Log = LogManager.getLogger(ServiceElasticSearchTest.class);

  /**
   * Connects to the database an deletes all records if any exist
   *
   * @throws URISyntaxException for incorrect server name
   * @throws IOException for error in deleting all records
   */
  @Before
  public void setUp() throws URISyntaxException, IOException {
    connectDB connect = new connectDB();
    client = connect.connect();
    client.deleteAllRecords();
  }

  /**
   * closes the connection with the database
   *
   * @throws IOException if error in closing connection to the database
   */
  @After
  public void tearDown() throws IOException {
    client.closeConnection();
  }

  /**
   * creates a message and adds it to the database
   *
   * @throws IOException If error entering data into the database
   * @throws DuplicateEntryException If message being added already exists in the database
   */
  private void queryAndPublishService() throws IOException, DuplicateEntryException {
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message.add("test-id", String.valueOf(1));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.toString());

    Message addedMessage = client.queryAndPublishService(message);
  }

  /**
   * Test to add a single record to the database
   *
   * @throws IOException If error inserting message into database
   * @throws DuplicateEntryException If the record already exists in the database
   */
  @Test
  public void queryAndPublishSingle() throws IOException, DuplicateEntryException {
    queryAndPublishService();
  }

  /** Check if duplicate entry exception is thrown when 2 dame records are added to the dastabase */
  @Test
  public void queryAndPublishExists() {
    boolean checkSecond = false;
    try {
      queryAndPublishService();
      checkSecond = true;
      Thread.sleep(1000); // Buffer time for adding record to
      queryAndPublishService();
      fail();
    } catch (DuplicateEntryException e) {
      if (checkSecond) {
        Log.info("Duplicate entry detected. Test passed");
      } else {
        Log.error("entry already exists before test");
        fail();
      }
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * Delete an existing record using it's URI from the database
   *
   * @throws IOException If unable to delete record from database
   * @throws DuplicateEntryException The record already exists before testing
   */
  @Test
  public void deleteExistingUri() throws IOException, DuplicateEntryException, RecordNotFoundException {
    this.queryAndPublishService();
    Message status = client.deleteRecord("2");
    assertNotNull(status.getMap());
  }

  /**
   * Attempt to delete a URI that doesn't exist in the database
   *
   * @throws IOException Error deleting the record
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void deleteNonExistingUri() throws IOException, DuplicateEntryException, RecordNotFoundException {
    this.queryAndPublishService();
    Message status = null;
    try {
      status = client.deleteRecord("3");
    } catch (RecordNotFoundException e) {
      Log.info("Couldn't find URI, test pass");
    }
    assertNull(status);
  }

  /**
   * Gets a record that exists in the database using the URI
   *
   * @throws IOException Error deleting the record
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void getExistingRecord() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message response = client.getRecordByURI("2");
    assertNotNull(response.getMap());
  }

  /**
   * Attempt to retrieve a record that doesn't exist in the database
   *
   * @throws IOException Error deleting the record
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void getNonExistingRecord() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message response = client.getRecordByURI("4");
    assertNull(response);
  }

  /**
   * Updates record that exists in the database using it's URI
   *
   * @throws IOException Error deleting the record
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void updateExisting() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message.add("test-id", String.valueOf(2));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.toString());

    Message response = client.updateService("2", message);
    assertNotNull(response.getMap());
  }

  /**
   * Attempt to update a record that doesn't exist in the database
   *
   * @throws IOException Error deleting the record
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void updateNotExisting() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message.add("test-id", String.valueOf(2));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.toString());

    try {
      Message response = client.updateService("3", message);
    } catch (IOException e) {
      Log.info("Test passed, database exception was thrown for missing service ID in database");
      assert (true);
    }
  }

  /**
   * Trying to update with null URI specified
   *
   * @throws IOException Error deleting the record
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void updateEmptyServiceID() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message.add("test-id", String.valueOf(2));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.toString());

    try {
      Message response = client.updateService(null, message);
    } catch (IOException e) {
      Log.info("Test passed, database exception was thrown for empty service ID");
      assert (true);
    }
  }

  /**
   * Attempts to add a record that doesn't already exist to database Shouldn't check for duplicates
   *
   * @throws IOException Error adding record
   */
  @Test
  public void publishServiceNotExistingTest() throws IOException {
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message.add("test-id", String.valueOf(2));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.toString());
    client.publishService(message);
    Message response = client.getRecordByURI("2");
    assertNotNull(response.getMap());
  }

  /**
   * Attempts to add a record that already exists to database shouldn't check for duplicates
   *
   * @throws IOException Error adding record to database
   */
  @Test
  public void publishServiceExistingTest() throws IOException {
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message.add("test-id", String.valueOf(2));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.toString());

    client.publishService(message);
    client.publishService(message);
    Message response = client.getRecordByURI("2");
    assertNotNull(response.getMap());
  }

  /**
   * Bulk update records that exist in the database
   *
   * @throws IOException unable to update records
   */
  @Test
  public void bulkUpdateAllExisting() throws IOException {
    Message message1 = new Message();
    message1.add("type", "test");

    message1.add(
        "uri", "1"); // 2nd param should be uuid but for testing purposes was assigned a number

    message1.add("test-id", String.valueOf(1));

    message1.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message1.add("expires", dateTime.toString());

    Message message2 = new Message();
    message1.add("type", "test");

    message2.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message2.add("test-id", String.valueOf(2));

    message2.add("ttl", "PT10M");

    message2.add("expires", dateTime.toString());

    Message message3 = new Message();
    message3.add("type", "test");

    message3.add(
        "uri", "3"); // 2nd param should be uuid but for testing purposes was assigned a number

    message3.add("test-id", String.valueOf(3));

    message3.add("ttl", "PT10M");

    message3.add("expires", dateTime.toString());

    client.publishService(message1);
    client.publishService(message2);
    client.publishService(message3);

    Map<String, Message> messages = new HashMap<String, Message>();
    messages.put("1", message2);
    messages.put("2", message3);
    messages.put("3", message1);

    Message count = client.bulkUpdate(messages);
    assertEquals(count.getMap().get("renewed"), 3);
  }

  /**
   * Attempt to update records that don't exist in the database
   *
   * @throws IOException Error updating records in the database
   */
  @Test
  public void bulkUpdateNotExisting() throws IOException {
    Message message1 = new Message();
    message1.add("type", "test");

    message1.add(
        "uri", "1"); // 2nd param should be uuid but for testing purposes was assigned a number

    message1.add("test-id", String.valueOf(1));

    message1.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message1.add("expires", dateTime.toString());

    Message message2 = new Message();
    message1.add("type", "test");

    message2.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message2.add("test-id", String.valueOf(2));

    message2.add("ttl", "PT10M");

    message2.add("expires", dateTime.toString());

    Message message3 = new Message();
    message3.add("type", "test");

    message3.add(
        "uri", "3"); // 2nd param should be uuid but for testing purposes was assigned a number

    message3.add("test-id", String.valueOf(3));

    message3.add("ttl", "PT10M");

    message3.add("expires", dateTime.toString());

    client.publishService(message1);
    client.publishService(message2);
    client.publishService(message3);

    Map<String, Message> messages = new HashMap<String, Message>();
    messages.put("1", message2);
    messages.put("2", message3);
    messages.put("4", message1);

    try {
      Message count = client.bulkUpdate(messages);
    } catch (IOException e) {
      Log.info("error updating due to incorrect URI; Passed test");
    }
  }

  /**
   * Delete records that have expired
   *
   * @throws IOException error deleting records
   * @throws InterruptedException Deletion process interrupted
   */
  @Test
  public void deleteExpired() throws IOException, InterruptedException {
    Message message1 = new Message();
    message1.add("type", "test");

    message1.add(
        "uri", "1"); // 2nd param should be uuid but for testing purposes was assigned a number

    message1.add("test-id", String.valueOf(1));

    message1.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message1.add("expires", dateTime.toString());

    Message message2 = new Message();
    message1.add("type", "test");

    message2.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message2.add("test-id", String.valueOf(2));

    message2.add("ttl", "PT10M");

    message2.add("expires", dateTime.toString());

    Message message3 = new Message();
    message3.add("type", "test");

    message3.add(
        "uri", "3"); // 2nd param should be uuid but for testing purposes was assigned a number

    message3.add("test-id", String.valueOf(3));

    message3.add("ttl", "PT10M");

    message3.add("expires", dateTime.toString());

    client.publishService(message1);
    client.publishService(message2);
    client.publishService(message3);

    Thread.sleep(2000);

    DateTime dt = new DateTime();
    dt.plus(20000);
    assertEquals(client.deleteExpiredRecords(dt), 3);
  }

  /**
   * Finds records in a given time range
   *
   * @throws IOException Error accessing records
   * @throws InterruptedException Accessing records interrupted
   */
  @Test
  public void findRecordInTimeRange() throws IOException, InterruptedException {
    Message message1 = new Message();
    message1.add("type", "test");

    message1.add(
        "uri", "1"); // 2nd param should be uuid but for testing purposes was assigned a number

    message1.add("test-id", String.valueOf(1));

    message1.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message1.add("expires", dateTime.toString());

    Message message2 = new Message();
    message1.add("type", "test");

    message2.add(
        "uri", "2"); // 2nd param should be uuid but for testing purposes was assigned a number

    message2.add("test-id", String.valueOf(2));

    message2.add("ttl", "PT10M");

    message2.add("expires", dateTime.toString());

    Message message3 = new Message();
    message3.add("type", "test");

    message3.add(
        "uri", "3"); // 2nd param should be uuid but for testing purposes was assigned a number

    message3.add("test-id", String.valueOf(3));

    message3.add("ttl", "PT10M");

    message3.add("expires", dateTime.toString());

    client.publishService(message1);
    client.publishService(message2);
    client.publishService(message3);

    Thread.sleep(3000);

    DateTime dt = new DateTime();
    dt.plus(2000000);
    dateTime.minus(10000);
    assertEquals(3, client.findRecordsInTimeRange(dateTime, dt).size());
  }

  /**
   * Get value of key that exists in the record of the given URI
   *
   * @throws IOException Unable to access record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void getKeyExists() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message record = client.getRecordByURI("2");
    String key = "test-id";
    Map<String, Object> keyValueMap = new HashMap<String, Object>();
    keyValueMap.put(key, record.getKey(key));
    assertEquals("1", keyValueMap.get("test-id"));
  }

  /**
   * Get value of key that doesn't exist in record of the given URI
   *
   * @throws IOException Unable to access record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void getKeyNotExists() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message record = client.getRecordByURI("2");
    String key = "random";
    Map<String, Object> keyValueMap = new HashMap<String, Object>();
    keyValueMap.put(key, record.getKey(key));
    assertNull(keyValueMap.get("random"));
  }
}
