package net.es.lookup.resources;

import com.google.gson.Gson;
import net.es.lookup.common.Message;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.internal.DatabaseException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class MainResourceTest {

  private ServiceElasticSearch client;

  private static Logger Log = LogManager.getLogger(RecordResourceTest.class);

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
   * Curl request to add a record to database that doesn't already exist
   *
   * @throws IOException // Error getting result from database
   * @throws InterruptedException // Sleep interrupted
   */
  @Test
  public void postHandlerNotExist() throws IOException, InterruptedException {
    MainResource request = new MainResource();
    String added = request.postHandler("lookup", jsonMessage(1));

    Map<String, String> map = getStringMap(added);

    Thread.sleep(1000);
    Message result = client.getRecordByURI(map.get("uri"));
    assertEquals(result.getMap().get("test-id").toString(), map.get("test-id"));
  }

  /**
   * Curl request to add a record to database that already exists
   *
   * @throws InterruptedException // Sleep interrupted
   */
  @Test
  public void postHandlerExist() throws InterruptedException {
    MainResource request = new MainResource();
    try {
      request.postHandler("lookup", jsonMessage(1));
      Thread.sleep(1000);
      request.postHandler("lookup", jsonMessage(1));
      Log.error("Should have given ForbiddenRequestException");
      fail();
    } catch (ForbiddenRequestException e) {
      Log.info("Record already exists, pass");
    }
  }

  /**
   * Both URI's to be updated already exist in the database
   */
  @Test
  public void bulkRenewHandlerExistingURI() {
    MainResource request = new MainResource();
    String added1 = request.postHandler("lookup", jsonMessage(1));
    String added2 = request.postHandler("lookup", jsonMessage(2));

    Map<String, String> map1 = getStringMap(added1);
    Map<String, String> map2 = getStringMap(added2);
    String[] uriList = new String[2];
    uriList[0] = "\""+map1.get("uri")+"\"";
    uriList[1] = "\""+map2.get("uri")+"\"";

    StringBuilder sb = new StringBuilder("{\"record-uris\":[");
    for(int i = 0; i < uriList.length; i++) {
      sb.append(uriList[i]);
      if(i < uriList.length-1) {
        sb.append(",");
      }
    }
    sb.append("]}");
    String response = request.bulkRenewHandler(sb.toString());
    Map<String, String> responseMap = this.getStringMap(response);
    assertEquals("2", responseMap.get("renewed"));
  }

  /**
   * Only 1 of the documents to be updated exists in the database
   */
  @Test
  public void bulkRenewHandlerNotExistingURI() {
    MainResource request = new MainResource();
    String added1 = request.postHandler("lookup", jsonMessage(1));
    String added2 = request.postHandler("lookup", jsonMessage(2));

    Map<String, String> map1 = getStringMap(added1);
    Map<String, String> map2 = getStringMap(added2);
    String[] uriList = new String[2];
    uriList[0] = "\""+map1.get("uri")+"\"";
    uriList[1] = "\""+map2.get("uri")+"fail\"";

    StringBuilder sb = new StringBuilder("{\"record-uris\":[");
    for(int i = 0; i < uriList.length; i++) {
      sb.append(uriList[i]);
      if(i < uriList.length-1) {
        sb.append(",");
      }
    }
    sb.append("]}");
    String response = request.bulkRenewHandler(sb.toString());
    Map<String, String> responseMap = this.getStringMap(response);
    assertEquals("1", responseMap.get("renewed"));
  }

  /**
   * Creates a json message
   *
   * @return json Message as string
   */
  private String jsonMessage(int seed) {
    Message message = new Message();
    message.add("type", "test");

    String uuid = UUID.randomUUID().toString();
    message.add(
        "uri",
        "lookup/interface/"+seed); // 2nd param should be uuid but for testing purposes was assigned a
    // number

    message.add("test-id", String.valueOf(seed));

    message.add("ttl", "PT10M");

    DateTime dateTime = new DateTime();
    message.add("expires", dateTime.plus(10000).plus(seed).toString());
    Gson gson = new Gson();
    return gson.toJson(message.getMap());
  }

  private Map<String, String> getStringMap(String added) {
    // Remove extra characters from string
    added = added.substring(1, added.length() - 1);
    added = added.replaceAll("\"", "");

    // Convert string to map
    Map<String, String> map = new HashMap<>();
    for (final String entry : added.split(",")) {
      final String[] parts = entry.split(":");
      map.put(parts[0], parts[1]);
    }
    return map;
  }
}
