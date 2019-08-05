package net.es.lookup.api;

import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceElasticSearch;
import net.es.lookup.database.connectDB;
import net.es.lookup.protocol.json.JSONRenewRequest;
import net.es.lookup.protocol.json.JsonBulkRegisterRequest;
import net.es.lookup.service.LookupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;

public class BulkRegisterService {

  private static Logger LOG = LogManager.getLogger(BulkRegisterService.class);

  public String bulkRegister(String messages) {

    JsonBulkRegisterRequest jsonBulkRegisterRequest = new JsonBulkRegisterRequest();
    if (messages.isEmpty()) {

      LOG.error("net.es.lookup.api.BulkRegisterService: Empty bulk request received");
      throw new BadRequestException("Request cannot be empty");
    }

    if (jsonBulkRegisterRequest.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {

      LOG.error("net.es.lookup.api.BulkRenewService: Request format is invalid.");
      throw new BadRequestException("Request is invalid. Please edit the request and resend.");
    }

    List<Message> messageList = jsonBulkRegisterRequest.parseJson(messages);
    List<String> failed = new ArrayList<>();
    Queue<Message> messageQueue = new LinkedList<>(messageList);
    connectDB connect = new connectDB();

    for (Message message : messageQueue) {
      boolean gotLease = LeaseManager.getInstance().requestLease(message);
      // System.out.println(message.getMap());
      if (gotLease) {

        String recordType = message.getRecordType();

        String uri = this.newUri(recordType);
        jsonBulkRegisterRequest.add(ReservedKeys.RECORD_URI, uri);

        // Add the state
        jsonBulkRegisterRequest.add(
            ReservedKeys.RECORD_STATE, ReservedValues.RECORD_VALUE_STATE_REGISTER);
      }
    }
    try {
      ServiceElasticSearch db = connect.connect();
      failed.addAll(db.bulkQueryAndPublishService(messageQueue));
    } catch (URISyntaxException e) {
      throw new InternalErrorException("Incorrect URI for bulkRegisterService" + e.getMessage());
    } catch (IOException e) {
      throw new InternalErrorException("Error connecting to database" + e.getMessage());
    } catch (DuplicateEntryException e) {
      throw new InternalErrorException(
          "Attempt to insert duplicate record using bulkRegisterService" + e.getMessage());
    }
    return failed.toString();
  }

  private String newUri(String recordType) {

    if (recordType != null && !recordType.isEmpty()) {
      String uri =
          LookupService.SERVICE_URI_PREFIX + "/" + recordType + "/" + UUID.randomUUID().toString();
      return uri;
    } else {
      LOG.error("Error creating URI: Record Type not found");
      throw new BadRequestException("Cannot create URI. Record Type not found");
    }
  }
}
