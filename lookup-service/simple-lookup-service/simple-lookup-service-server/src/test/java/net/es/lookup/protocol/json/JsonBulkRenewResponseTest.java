package net.es.lookup.protocol.json;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.sf.json.JSONArray;
import org.junit.Test;

public class JsonBulkRenewResponseTest {

  @Test
  public void testJsonBulkRenewResponseSuccess() {
    System.out.println("Testing JSON Bulk Renew Response Creation - Success Message");
    String bulkRenewal =
        "{'record-uris':['lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";

    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(bulkRenewal);
    List<String> allRecordUris = (List) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);

    Message renewedUris = new Message();
    renewedUris.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, allRecordUris.size());

    JsonBulkRenewResponse jsonBulkRenewResponse = new JsonBulkRenewResponse();
    jsonBulkRenewResponse.addTotalRecordsCount(allRecordUris.size());
    jsonBulkRenewResponse.updateRenewedCount(renewedUris);
    jsonBulkRenewResponse.updateFailures(new HashMap<String, Message>());

    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_TOTALRECORDS);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_FAILUREURIS);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.ERROR_MESSAGE);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.ERROR_CODE);

    assertEquals(
        allRecordUris.size(),
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_TOTALRECORDS)));
    assertEquals(
        allRecordUris.size(),
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT)));
    assertEquals(
        0,
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)));

    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILUREURIS)).size());
    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.ERROR_CODE)).size());
    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.ERROR_MESSAGE)).size());
  }

  @Test
  public void testJsonBulkRenewResponseAllFailures() {
    System.out.println("Testing JSON Bulk Renew Response Creation- All failures Message");
    String bulkRenewal =
        "{'record-uris':['lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";

    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(bulkRenewal);
    List<String> allRecordUris =
        (List<String>) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);

    Map<String, Message> failedUris = new HashMap<>();
    Message errorMessage = new Message();
    errorMessage.add(ReservedKeys.ERROR_CODE, 21);
    errorMessage.add(
        ReservedKeys.ERROR_MESSAGE, ReservedValues.RECORD_BULKRENEW_EXPIRED_ERRORMESSAGE);
    for (int i = 0; i < allRecordUris.size(); i++) {
      failedUris.put(allRecordUris.get(i), errorMessage);
    }

    Message renewedUris = new Message();
    renewedUris.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, 0);

    JsonBulkRenewResponse jsonBulkRenewResponse = new JsonBulkRenewResponse();
    jsonBulkRenewResponse.addTotalRecordsCount(allRecordUris.size());
    jsonBulkRenewResponse.updateRenewedCount(renewedUris);
    jsonBulkRenewResponse.updateFailures(failedUris);

    assertEquals(
        allRecordUris.size(),
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_TOTALRECORDS)));
    assertEquals(
        0,
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT)));
    assertEquals(
        allRecordUris.size(),
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)));

    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILUREURIS)).size());
    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.ERROR_CODE)).size());
    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.ERROR_MESSAGE)).size());
  }

  @Test
  public void testJsonBulkRenewResponsePartialFailures() {
    System.out.println("Testing JSON Bulk Renew Response Creation - Partial Failures");
    String bulkRenewal =
        "{'record-uris':['lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";

    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(bulkRenewal);
    List<String> allRecordUris =
        (List<String>) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);

    Message renewedUris = new Message();
    renewedUris.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, 1);

    Map<String, Message> failedUris = new HashMap<>();
    Message errorMessage = new Message();
    errorMessage.add(ReservedKeys.ERROR_CODE, 21);
    errorMessage.add(
        ReservedKeys.ERROR_MESSAGE, ReservedValues.RECORD_BULKRENEW_EXPIRED_ERRORMESSAGE);
    failedUris.put(allRecordUris.get(1), errorMessage);

    JsonBulkRenewResponse jsonBulkRenewResponse = new JsonBulkRenewResponse();
    jsonBulkRenewResponse.addTotalRecordsCount(allRecordUris.size());
    jsonBulkRenewResponse.updateRenewedCount(renewedUris);
    jsonBulkRenewResponse.updateFailures(failedUris);

    assertEquals(
        allRecordUris.size(),
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_TOTALRECORDS)));
    assertEquals(
        1,
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT)));
    assertEquals(
        1,
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)));

    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILUREURIS)).size());
    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.ERROR_CODE)).size());
    assertEquals(
        Integer.parseInt(
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.ERROR_MESSAGE)).size());
  }
}
