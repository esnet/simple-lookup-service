package net.es.lookup.resources;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceElasticSearch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assert.*;

public class KeyResourceTest {

  private ServiceElasticSearch client;

  private static Logger Log = LogManager.getLogger(KeyResourceTest.class);

  @BeforeClass
  public static void setUpDatabase() throws DatabaseException, URISyntaxException {
    new ServiceElasticSearch("localhost", 9200, 9300, "lookup");
  }

  /**
   * Connects to the database an deletes all records if any exist
   *
   * @throws IOException for error in deleting all records
   */
  @Before
  public void setUp() throws IOException {
    client = ServiceElasticSearch.getInstance();
    client.deleteAllRecords();
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
            "uri",
            "lookup/interface/2"); // 2nd param should be uuid but for testing purposes was assigned a
    // number

    message.add("test-id", String.valueOf(1));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.plus(1000000).toString());

    Message addedMessage = client.queryAndPublishService(message);
  }

  /**
   * Curl request for getKey where key exists
   * @throws IOException Error reading or writing to database
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void getHandlerKeyExists() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    KeyResource request = new KeyResource();
    String result = request.getHandler("lookup", "interface", "2", "test-id");
    assertNotNull(result);
  }

  /**
   * Curl request for getKey where key doesn't exists
   *
   * @throws IOException Error reading or writing to database
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void getHandlerKeyNotExists() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    KeyResource request = new KeyResource();
    try{
    request.getHandler("lookup", "interface", "2", "test-id2");
    Log.error("Should have thrown NotFoundException");
    fail();
    }catch (NotFoundException e){
      Log.info("Key not found, test pass");
    }
  }

  /**
   * Curl request for getKey where index doesn't exist
   *
   * @throws IOException Error reading or writing to database
   * @throws DuplicateEntryException Entry already exists before test
   */
  @Test
  public void getHandlerURINotExists() throws IOException, DuplicateEntryException {
    this.queryAndPublishService();
    KeyResource request = new KeyResource();
    try{
      request.getHandler("lookup", "interface", "3", "test-id2");
      Log.error("Should have thrown NotFoundException");
      fail();
    }catch (NotFoundException e){
      Log.info("Record not found, test pass");
    }
  }
}