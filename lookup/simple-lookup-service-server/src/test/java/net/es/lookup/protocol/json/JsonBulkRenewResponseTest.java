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
    String bulkRenewal =
        "{'record-uris':['lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";

    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(bulkRenewal);
    JSONArray allRecordUris =
        (JSONArray) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);

    List<String> renewedUris = new ArrayList<>();
    for (int i = 0; i < allRecordUris.size(); i++) {
      renewedUris.add(allRecordUris.getString(i));
    }

    JsonBulkRenewResponse jsonBulkRenewResponse = new JsonBulkRenewResponse();
    jsonBulkRenewResponse.addTotalRecordsCount(allRecordUris.size());
    jsonBulkRenewResponse.updateRenewed(renewedUris);
    jsonBulkRenewResponse.updateFailures(new HashMap<String, Message>());

    try {
      System.out.println(JSONMessage.toString(jsonBulkRenewResponse));
    } catch (DataFormatException e) {
      e.printStackTrace();
    }

    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_TOTALRECORDS);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_FAILURECOUNT);
    assert jsonBulkRenewResponse.hasKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDURIS);
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
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDURIS)).size());
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
    String bulkRenewal =
        "{'record-uris':['lookup/psmetadata/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";

    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(bulkRenewal);
    JSONArray allRecordUris =
        (JSONArray) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);

    Map<String, Message> failedUris = new HashMap<>();
    Message errorMessage = new Message();
    errorMessage.add(ReservedKeys.ERROR_CODE, 21);
    errorMessage.add(ReservedKeys.ERROR_MESSAGE, ReservedValues.RECORD_BULKRENEW_EXPIRED_ERRORMESSAGE);
    for (int i = 0; i < allRecordUris.size(); i++) {
      failedUris.put(allRecordUris.getString(i), errorMessage);
    }

    JsonBulkRenewResponse jsonBulkRenewResponse = new JsonBulkRenewResponse();
    jsonBulkRenewResponse.addTotalRecordsCount(allRecordUris.size());
    jsonBulkRenewResponse.updateRenewed(new ArrayList<>());
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
            (String) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT)),
        ((List) jsonBulkRenewResponse.getKey(ReservedKeys.RECORD_BULKRENEW_RENEWEDURIS)).size());
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
