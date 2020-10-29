package net.es.lookup.resources;

import com.google.gson.Gson;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceElasticSearch;

import net.es.lookup.common.ReservedValues;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.URISyntaxException;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class RecordResourceTest {

  private static ServiceElasticSearch client;

  private static Logger Log = LogManager.getLogger(RecordResourceTest.class);

  /**
   * Connects to the database an deletes all records if any exist
   *
   * @throws DatabaseException for error in deleting all records
   */
  @Before
  public void setUp() throws DatabaseException {

    client = ServiceElasticSearch.getInstance();
    client.deleteAllRecords();
  }


  /**
   * creates a message and adds it to the database
   *
   * @throws DatabaseException If error entering data into the database
   * @throws DuplicateEntryException If message being added already exists in the database
   */
  private void queryAndPublishService() throws DatabaseException, DuplicateEntryException {

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
    Message query = new Message();
    query.add("type", "test");
    query.add("test-id", String.valueOf(1));

    Message operators = new Message();
    operators.add("type", ReservedValues.RECORD_OPERATOR_ALL);
    operators.add("test-id", ReservedValues.RECORD_OPERATOR_ALL);

    Message addedMessage = client.queryAndPublishService(message, query, operators);

  }

  /**
   * Testing curl GET request for existing record
   *
   * @throws DatabaseException Error in adding or looking up the record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void getHandlerExisting() throws DatabaseException, DuplicateEntryException {

    this.queryAndPublishService();
    RecordResource request = new RecordResource();
    String output = request.getHandler("lookup", "interface", "2");
    assertNotNull(output);
  }

  /**
   * Testing curl GET request for a record that doesn['t exist
   *
   * @throws DatabaseException Error in adding or looking up the record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void getHandlerNotExisting() throws DatabaseException, DuplicateEntryException {
    this.queryAndPublishService();
    RecordResource request = new RecordResource();
    try {
      String output = request.getHandler("lookup", "interface", "3");
      fail();
    } catch (NotFoundException e) {
      Log.info("Record not found, test passed");
    }
  }

  /**
   * Curl request for renewing an existing URI
   *
   * @throws DatabaseException Error in adding or looking up the record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void renewHandlerExists() throws DatabaseException, DuplicateEntryException {

    this.queryAndPublishService();
    RecordResource request = new RecordResource();

    String output = request.renewHandler("lookup", "interface", "2", jsonMessage());

    assertNotNull(output);
  }

  /**
   * Curl request for renewing a URI that doesn't exist
   *
   * @throws DatabaseException Error in adding or looking up the record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void renewHandlerNotExists() throws DatabaseException, DuplicateEntryException {

    this.queryAndPublishService();
    RecordResource request = new RecordResource();

    try {
      String output = request.renewHandler("lookup", "interface", "3", jsonMessage());
      fail();
    } catch (NotFoundException e) {
      Log.info("URI not found, test passed");
    }
  }

  /**
   * Curl request for deleting existing URI
   *
   * @throws DatabaseException Error in adding or looking up the record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void deleteHandlerExists() throws DatabaseException, DuplicateEntryException {
    this.queryAndPublishService();
    RecordResource request = new RecordResource();
    String response = request.deleteHandler("lookup", "interface", "2", jsonMessage());
    assertNotNull(response);
  }

  /**
   * Curl request for deleting non existing URI
   *
   * @throws DatabaseException Error in adding or looking up the record
   * @throws DuplicateEntryException Record already exists before test
   */
  @Test
  public void deleteHandlerNotExists() throws DatabaseException, DuplicateEntryException {
    this.queryAndPublishService();
    RecordResource request = new RecordResource();
    try {
      String response = request.deleteHandler("lookup", "interface", "3", jsonMessage());
      fail();
    } catch (NotFoundException e) {
      Log.info("URI not found, test passed");
    }
  }

  /**
   * Creates a json message
   *
   * @return json Message as string
   */
  private String jsonMessage() {
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
    message.add("expires", dateTime.toString());
    Gson gson = new Gson();
    return gson.toJson(message.getMap());
  }
}
