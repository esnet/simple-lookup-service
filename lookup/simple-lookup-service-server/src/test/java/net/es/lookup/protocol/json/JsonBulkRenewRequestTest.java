package net.es.lookup.protocol.json;

import static net.sf.ezmorph.test.ArrayAssertions.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.List;
import net.es.lookup.common.ReservedKeys;
import net.sf.json.JSONArray;
import net.sf.json.JSONString;
import org.junit.Test;

public class JsonBulkRenewRequestTest {

  @Test
  public void testBulkRenewRequestParser() {
    System.out.println("Testing JSON Bulk Renew Request Parser - Base Test");
    String bulkRenewal =
        "{'record-uris':['lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";

    String[] expectedRecordUri = {
      "lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b",
      "lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj"
    };
    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(bulkRenewal);
    Object ttlvalue = jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_TTL);
    assert ttlvalue.toString().contentEquals("PT2H");

    List<String> recordUris =
        (List<String>) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);
    String[] actualUris = new String[recordUris.size()];

    recordUris.toArray(actualUris);
    assertArrayEquals(expectedRecordUri, actualUris);
  }

  @Test
  public void testBulkRenewRequestStringParser() {
    System.out.println("Testing JSON Bulk Renew Request Parser - single uri");
    String bulkRenewal =
        "{'record-uris':'lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b', 'ttl': 'PT2H'}";

    String expectedRecordUri = "lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b";
    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(bulkRenewal);
    Object ttlvalue = jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_TTL);
    assert ttlvalue.toString().contentEquals("PT2H");

    String actualUris = (String) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);
    assertEquals(expectedRecordUri, actualUris);
  }
}
