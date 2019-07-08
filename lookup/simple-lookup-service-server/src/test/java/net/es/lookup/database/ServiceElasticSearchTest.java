package net.es.lookup.database;

import net.es.lookup.common.DatabaseConnectionKeys;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class ServiceElasticSearchTest {
  private ServiceElasticSearch client;

  @Before
  public void setUp() throws Exception {
    client =
        new ServiceElasticSearch(
            DatabaseConnectionKeys.server,
            DatabaseConnectionKeys.DatabasePort1,
            DatabaseConnectionKeys.DatabasePort2,
            DatabaseConnectionKeys.DatabaseName);
    client.deleteAllRecords();
  }

  @After
  public void tearDown() throws Exception {
    client.closeConnection();
  }

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
    // System.out.println(addedMessage.getMap().toString());
  }

  @Test
  public void queryAndPublishSingle() throws IOException, DuplicateEntryException {
    queryAndPublishService();
  }

  @Test
  // Fix exists
  public void queryAndPublishExists() {
    boolean checkSecond = false;
    try {
      queryAndPublishService();
      checkSecond = true;
      Thread.sleep(1000);
      queryAndPublishService();
      fail();
    } catch (DuplicateEntryException e) {
      if (checkSecond) {
        System.out.println("Duplicate entry detected. Test passed");
      } else {
        System.out.println("entry already exists before test");
        fail();
      }
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void deleteExistingUri() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message status = client.deleteRecord("2");
    assertNotNull(status.getMap());
  }

  @Test
  public void deleteNonExistingUri() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message status = null;
    try {
      status = client.deleteRecord("3");
    } catch (IOException e) {
      System.out.println("Couldn't find URI, test pass");
    }
    assertNull(status);
  }

  @Test
  public void getExistingRecord() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message response = client.getRecordByURI("2");
    assertNotNull(response.getMap());
  }

  @Test
  public void getNonExistingRecord() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    Message response = client.getRecordByURI("4");
    assertNull(response);
  }

  @Test
  public void updateExisting() throws IOException, DuplicateEntryException, DatabaseException {
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

  @Test
  public void updateNotExisting() throws IOException, DuplicateEntryException, DatabaseException {
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
    } catch (DatabaseException e) {
      System.out.println(
          "Test passed, database exception was thrown for missing service ID in database");
      assert (true);
    }
  }

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
    } catch (DatabaseException e) {
      System.out.println("Test passed, database exception was thrown for empty service ID");
      assert (true);
    }
  }

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
      System.out.println("error updating due to incorrect URI; Passed test");
    }
  }

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
}
