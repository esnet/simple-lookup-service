package net.es.lookup.resources;

import com.google.gson.Gson;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceElasticSearch;
import net.es.lookup.database.connectDB;
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

public class MainResourceTest {

  private ServiceElasticSearch client;

  private static Logger Log = LogManager.getLogger(RecordResourceTest.class);

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
   * Curl request to add a record to database that doesn't already exist
   *
   * @throws IOException // Error getting result from database
   * @throws InterruptedException // Sleep interrupted
   */
  @Test
  public void postHandlerNotExist() throws IOException, InterruptedException {
    MainResource request = new MainResource();
    String added = request.postHandler("lookup", jsonMessage());

    // Remove extra characters from string
    added = added.substring(1, added.length() - 1);
    added = added.replaceAll("\"", "");

    // Convert string to map
    Map<String, String> map = new HashMap<String, String>();
    for (final String entry : added.split(",")) {
      final String[] parts = entry.split(":");
      map.put(parts[0], parts[1]);
    }

    Thread.sleep(1000);
    Message result = client.getRecordByURI(map.get("uri"));
    assertEquals("[1]", map.get("test-id"));
  }

  /**
   * Curl request to add a record to database that already exists
   *
   * @throws IOException // Error getting result from database
   * @throws InterruptedException // Sleep interrupted
   */
  @Test
  public void postHandlerExist() throws InterruptedException {
    MainResource request = new MainResource();
    try {
      request.postHandler("lookup", jsonMessage());
      Thread.sleep(1000);
      request.postHandler("lookup", jsonMessage());
      Log.error("Should have given ForbiddenRequestException");
      fail();
    } catch (ForbiddenRequestException e) {
      Log.info("Record already exists, pass");
    }
  }

  @Test
  public void getHandler() {
    // Todo ??
  }

  @Test
  public void bulkRenewHandler() {}

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
    message.add("expires", dateTime.plus(10000).toString());
    Gson gson = new Gson();
    return gson.toJson(message.getMap());
  }
}
