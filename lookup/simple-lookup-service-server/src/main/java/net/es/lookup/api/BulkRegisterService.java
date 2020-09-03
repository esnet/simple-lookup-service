package net.es.lookup.api;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceElasticSearch;
import net.es.lookup.protocol.json.JsonBulkRegisterRequest;
import net.es.lookup.service.LookupService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BulkRegisterService {

  private static Logger LOG = LogManager.getLogger(BulkRegisterService.class);

  /**
   * Takes a JSON string of messages and returns the list of failed messages.
   *
   * @param messages JSON String
   * @return String of all messages that were failed; {} if nothing failed
   */
  public String bulkRegister(String messages) {

    JsonBulkRegisterRequest jsonBulkRegisterRequest = new JsonBulkRegisterRequest();
    if (messages.isEmpty()) {

      LOG.error("net.es.lookup.api.BulkRegisterService: Empty bulk request received");
      throw new BadRequestException("Request cannot be empty");
    }

    if (jsonBulkRegisterRequest.getStatus() == JsonBulkRegisterRequest.INCORRECT_FORMAT) {

      LOG.error("net.es.lookup.api.BulkRegisterService: Request format is invalid.");
      throw new BadRequestException("Request is invalid. Please edit the request and resend.");
    }
    List<Message> messageList =
        jsonBulkRegisterRequest.parseJson(
            messages); // List of messages to be added after parsing json
    List<Message> failed;
    Queue<Message> messageQueue = new LinkedList<>(messageList);

    for (Message message : messageQueue) {
      boolean gotLease = LeaseManager.getInstance().requestLease(message);
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

      ServiceElasticSearch db = ServiceElasticSearch.getInstance();
      failed = db.bulkQueryAndPublishService(messageQueue);

    } catch (DatabaseException e) {
      throw new InternalErrorException("Error connecting to database: " + e.getMessage());
    }
    StringBuilder failedStrings = new StringBuilder();
    failedStrings.append("failures: [");
    for (Message fail : failed) {
      failedStrings.append(fail.getMap()).append("\n\n");
    }
    failedStrings.append(']');
    return failedStrings.toString();
  }

  private String newUri(String recordType) {

    if (recordType != null && !recordType.isEmpty()) {
      return
          LookupService.SERVICE_URI_PREFIX + "/" + recordType + "/" + UUID.randomUUID().toString();
    } else {
      LOG.error("Error creating URI: Record Type not found");
      throw new BadRequestException("Cannot create URI. Record Type not found");
    }
  }
}
