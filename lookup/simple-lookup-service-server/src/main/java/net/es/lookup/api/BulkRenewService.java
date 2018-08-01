package net.es.lookup.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.ResponseCodes;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceDaoMongoDb;
import net.es.lookup.protocol.json.JSONRenewRequest;
import net.es.lookup.protocol.json.JsonBulkRenewRequest;
import net.es.lookup.protocol.json.JsonBulkRenewResponse;
import net.es.lookup.publish.Publisher;
import net.es.lookup.service.PublishService;
import org.apache.log4j.Logger;

public class BulkRenewService {
  private static Logger LOG = Logger.getLogger(BulkRenewService.class);

  /** The method bulk renews records. */
  public String bulkRenew(String dbname, String renewRequests) {

    // parse records
    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(renewRequests);

    if (renewRequests.isEmpty()) {

      LOG.error("net.es.lookup.api.BulkRenewService: Empty bulk request received");
      throw new BadRequestException("Request cannot be empty");
    }

    if (jsonBulkRenewRequest.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {

      LOG.error("Request format is invalid.");
      throw new BadRequestException("Request is invalid. Please edit the request and resend.");
    }

    renewRecords(dbname, jsonBulkRenewRequest);

    // convert to json response and send
    return null;
  }

  private Message getErrorRecord(int failureCode) {

    Message error = new Message();
    if (failureCode == ResponseCodes.ERROR_BULK_NOTFOUND) {
      error.add(ReservedKeys.ERROR_CODE, ResponseCodes.ERROR_BULK_NOTFOUND);
      error.add(ReservedKeys.ERROR_MESSAGE, ReservedValues.RECORD_BULKRENEW_NOTFOUND_ERRORMESSAGE);
    } else if (failureCode == ResponseCodes.ERROR_BULK_EXPIRED) {
      error.setError(ResponseCodes.ERROR_BULK_EXPIRED);
      error.setErrorMessage(ReservedValues.RECORD_BULKRENEW_EXPIRED_ERRORMESSAGE);
    }
    return error;
  }

  private void notifyPublisher(Map<String, Message> updates){
    if (PublishService.isServiceOn()) {

      Publisher publisher = Publisher.getInstance();
      for (Entry<String, Message> renewedRecord : updates.entrySet()) {

        publisher.eventNotification(renewedRecord.getValue());
      }
    }
  }

  private void renewRecords(String dbname, JsonBulkRenewRequest jsonBulkRenewRequest){
    // renew
    Map<String, Message> failedUris = new HashMap<>();

    try {
      ServiceDaoMongoDb db = ServiceDaoMongoDb.getInstance();
      if (db == null) {

        LOG.error(("Error accessing database object"));
        throw new InternalErrorException("Error accessing database");
      }

      String[] allRecordUris =
          (String[]) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);
      Map<String, Message> bulkUpdateRequests = new HashMap<>();

      for (String uri : allRecordUris) {

        Message serviceRecord = db.getRecordByURI(uri);

        if (serviceRecord == null) {

          Message error = getErrorRecord(ResponseCodes.ERROR_BULK_NOTFOUND);
          failedUris.put(uri, error);
          LOG.error("net.es.lookup.api.BulkRenewService Record uri not found: " + uri);
          continue;
        }

        Map<String, Object> serviceMap = serviceRecord.getMap();

        if (jsonBulkRenewRequest.getTTL() != null && !jsonBulkRenewRequest.getTTL().isEmpty()) {

          serviceMap.put(ReservedKeys.RECORD_TTL, jsonBulkRenewRequest.getTTL());
        } else {

          serviceMap.put(ReservedKeys.RECORD_TTL, new ArrayList());
        }

        Message newRequest = new Message(serviceMap);
        boolean gotLease = LeaseManager.getInstance().requestLease(newRequest);

        if (!gotLease) {

          Message error = getErrorRecord(ResponseCodes.ERROR_BULK_EXPIRED);
          failedUris.put(uri, error);
          LOG.error(
              "net.es.lookup.api.BulkRenewService  "
                  + "Failed to secure lease for record uri: "
                  + uri);
          continue;
        }

        newRequest.add(ReservedKeys.RECORD_STATE, ReservedValues.RECORD_VALUE_STATE_RENEW);

        bulkUpdateRequests.put(uri, newRequest);
      }

      // db call
      Map<String, Message> renewResponse = db.bulkUpdate(bulkUpdateRequests);

      notifyPublisher(renewResponse);

      List<String> renewedUris = new ArrayList<>(renewResponse.keySet());

      JsonBulkRenewResponse jsonBulkRenewResponse = new JsonBulkRenewResponse();
      jsonBulkRenewResponse.addTotalRecordsCount(allRecordUris.length);
      jsonBulkRenewResponse.updateFailures(failedUris);
      jsonBulkRenewResponse.updateRenewed(renewedUris);

    } catch (DatabaseException e) {

      LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
      LOG.info("RenewService status: FAILED; exiting");
      throw new InternalErrorException("Database error\n");
    }
  }
}
