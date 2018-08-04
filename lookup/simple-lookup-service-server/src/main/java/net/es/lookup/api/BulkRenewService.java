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
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceDaoMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRenewRequest;
import net.es.lookup.protocol.json.JsonBulkRenewRequest;
import net.es.lookup.protocol.json.JsonBulkRenewResponse;
import net.es.lookup.publish.Publisher;
import net.es.lookup.service.PublishService;
import org.apache.log4j.Logger;

public class BulkRenewService {
  private static Logger LOG = Logger.getLogger(BulkRenewService.class);

  /**
   * The method bulk renews records.
   * @param renewRequests Request containing list of uris.
   * @return String Json message as a string.
   *
   * */
  public String bulkRenew(String renewRequests) {

    // parse records
    JsonBulkRenewRequest jsonBulkRenewRequest = new JsonBulkRenewRequest(renewRequests);

    if (renewRequests.isEmpty()) {

      LOG.error("net.es.lookup.api.BulkRenewService: Empty bulk request received");
      throw new BadRequestException("Request cannot be empty");
    }

    if (jsonBulkRenewRequest.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {

      LOG.error("net.es.lookup.api.BulkRenewService: Request format is invalid.");
      throw new BadRequestException("Request is invalid. Please edit the request and resend.");
    }

    ServiceDaoMongoDb db = ServiceDaoMongoDb.getInstance();
    if (db == null) {

      LOG.error(("net.es.lookup.api.BulkRenewService: Error accessing database object"));
      throw new InternalErrorException("Error accessing database");
    }

    JsonBulkRenewResponse renewResponse = checkAndRenewRecords(db, jsonBulkRenewRequest);
    String formattedRenewResponse = "";
    try {
      formattedRenewResponse = JSONMessage.toString(renewResponse);
    } catch (DataFormatException e) {
      LOG.error(("net.es.lookup.api.BulkRenewService: Error formatting result"));
      throw new InternalErrorException(
          "Error formatting result. Ask administrator to check logs "
              + "to confirm status of renew operation");
    }

    // convert to json response and send
    return formattedRenewResponse;
  }

  private JsonBulkRenewResponse checkAndRenewRecords(
      ServiceDaoMongoDb db, JsonBulkRenewRequest jsonBulkRenewRequest) {
    // renew
    Map<String, Message> failedUris = new HashMap<>();

    try {
      List<String> allRecordUris =
          (List<String>) jsonBulkRenewRequest.getKey(ReservedKeys.RECORD_BULK_URIS);
      Map<String, Message> bulkUpdateRequests = new HashMap<>();

      for (String uri : allRecordUris) {

        Message serviceRecord = db.getRecordByUri(uri);

        if (serviceRecord == null) {

          Message error = createErrorRecord(ResponseCodes.ERROR_BULK_NOTFOUND);
          failedUris.put(uri, error);
          LOG.error("net.es.lookup.api.BulkRenewService Record uri not found: " + uri);
          continue;
        }

        Message serviceLeaseRenewalRequest =
            updateTtl(serviceRecord, jsonBulkRenewRequest.getTTL());
        boolean gotLease = LeaseManager.getInstance().requestLease(serviceLeaseRenewalRequest);

        if (!gotLease) {

          Message error = createErrorRecord(ResponseCodes.ERROR_BULK_EXPIRED);
          failedUris.put(uri, error);
          LOG.error(
              "net.es.lookup.api.BulkRenewService  "
                  + "Failed to secure lease for record uri: "
                  + uri);
          continue;
        }

        serviceLeaseRenewalRequest.add(
            ReservedKeys.RECORD_STATE, ReservedValues.RECORD_VALUE_STATE_RENEW);

        bulkUpdateRequests.put(uri, serviceLeaseRenewalRequest);
      }

      // db call
      Message renewResponse = db.bulkUpdate(bulkUpdateRequests);
      notifyPublisher(bulkUpdateRequests);

      JsonBulkRenewResponse jsonBulkRenewResponse =
          formatJsonBulkRenewResponse(allRecordUris.size(), renewResponse, failedUris);
      return jsonBulkRenewResponse;

    } catch (DatabaseException e) {

      LOG.fatal("DatabaseException: Error renewing services." + e.getMessage());
      LOG.info("RenewService status: FAILED; exiting");
      throw new InternalErrorException("Database error\n");
    }
  }

  private Message updateTtl(Message serviceRecord, String ttl) {
    Map<String, Object> serviceMap = serviceRecord.getMap();

    if (ttl != null && !ttl.isEmpty()) {

      serviceMap.put(ReservedKeys.RECORD_TTL, ttl);
    } else {

      serviceMap.put(ReservedKeys.RECORD_TTL, new ArrayList());
    }

    return new Message(serviceMap);
  }

  private Message createErrorRecord(int failureCode) {

    Message error = new Message();
    if (failureCode == ResponseCodes.ERROR_BULK_NOTFOUND) {
      error.add(ReservedKeys.ERROR_CODE, ResponseCodes.ERROR_BULK_NOTFOUND);
      error.add(ReservedKeys.ERROR_MESSAGE, ReservedValues.RECORD_BULKRENEW_NOTFOUND_ERRORMESSAGE);
    } else if (failureCode == ResponseCodes.ERROR_BULK_EXPIRED) {
      error.add(ReservedKeys.ERROR_CODE, ResponseCodes.ERROR_BULK_EXPIRED);
      error.add(ReservedKeys.ERROR_MESSAGE, ReservedValues.RECORD_BULKRENEW_EXPIRED_ERRORMESSAGE);
    }
    return error;
  }

  private void notifyPublisher(Map<String, Message> updates) {
    if (PublishService.isServiceOn()) {

      Publisher publisher = Publisher.getInstance();
      for (Entry<String, Message> renewedRecord : updates.entrySet()) {

        publisher.eventNotification(renewedRecord.getValue());
      }
    }
  }

  private JsonBulkRenewResponse formatJsonBulkRenewResponse(
      int totalRecords, Message renewResponse, Map<String, Message> failedUris) {

    JsonBulkRenewResponse jsonBulkRenewResponse = new JsonBulkRenewResponse();
    jsonBulkRenewResponse.addTotalRecordsCount(totalRecords);
    jsonBulkRenewResponse.updateFailures(failedUris);
    jsonBulkRenewResponse.updateRenewedCount(renewResponse);
    return jsonBulkRenewResponse;
  }
}
