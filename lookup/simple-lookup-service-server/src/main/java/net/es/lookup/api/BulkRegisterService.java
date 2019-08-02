package net.es.lookup.api;

import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.database.ServiceElasticSearch;
import net.es.lookup.database.connectDB;
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

    JsonBulkRegisterRequest request = new JsonBulkRegisterRequest();

    List<Message> messageList = request.parseJson(messages);
    List<String> failed = new ArrayList<>();
    Queue<Message> messageQueue = new LinkedList<>(messageList);
    connectDB connect = new connectDB();

    for (Message message : messageQueue) {
      boolean gotLease = LeaseManager.getInstance().requestLease(message);
      //System.out.println(message.getMap());
      if (gotLease) {

        String recordType = message.getRecordType();

        String uri = this.newUri(recordType);
        request.add(ReservedKeys.RECORD_URI, uri);

        // Add the state
        request.add(ReservedKeys.RECORD_STATE, ReservedValues.RECORD_VALUE_STATE_REGISTER);
      }
    }
      try {
          ServiceElasticSearch db = connect.connect();
          failed.addAll(db.bulkQueryAndPublishService(messageQueue));
      } catch (URISyntaxException e) {
          e.printStackTrace();
      } catch (IOException e) {
          e.printStackTrace();
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
