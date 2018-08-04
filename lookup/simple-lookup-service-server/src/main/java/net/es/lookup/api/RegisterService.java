package net.es.lookup.api;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.UnauthorizedException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.DuplicateEntryException;
import net.es.lookup.database.ServiceDaoMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRegisterRequest;
import net.es.lookup.protocol.json.JSONRegisterResponse;
import net.es.lookup.publish.Publisher;
import net.es.lookup.service.LookupService;
import net.es.lookup.service.PublishService;
import org.apache.log4j.Logger;


public class RegisterService {

  private static Logger LOG = Logger.getLogger(RegisterService.class);
  private String params;

  /**
   * Method to register the record.
   * @param message record to be registered
   * @return String registered record as Json message
   * */
  public String registerService(String message) {

    LOG.info(" Processing registerService.");
    LOG.info(" Received message: " + message);
    JSONRegisterResponse response;
    JSONRegisterRequest request = new JSONRegisterRequest(message);

    if (request.getStatus() == JSONRegisterRequest.INCORRECT_FORMAT) {

      LOG.info("Register status: FAILED; exiting");
      LOG.error("Incorrect JSON Data Format");
      throw new BadRequestException("Error parsing JSON elements.");
    }

    LOG.debug("valid?" + this.isValid(request));

    if (this.isValid(request) && this.isAuthed(request)) {

      // Request a lease
      boolean gotLease = LeaseManager.getInstance().requestLease(request);

      if (gotLease) {

        String recordType = request.getRecordType();

        String uri = this.newUri(recordType);
        request.add(ReservedKeys.RECORD_URI, uri);

        // Add the state
        request.add(ReservedKeys.RECORD_STATE, ReservedValues.RECORD_VALUE_STATE_REGISTER);

        // Build the matching query requestUrl that must fail for the service to be published
        Message query = new Message();
        Message operators = new Message();
        List<String> list;
        List<String> queryKeyList = new ArrayList();

        Map<String, Object> keyValues = request.getMap();
        Iterator it = keyValues.entrySet().iterator();

        while (it.hasNext()) {

          Map.Entry<String, Object> pairs = (Map.Entry) it.next();

          if (!isIgnoreKey(pairs.getKey())) {

            LOG.debug("key-value pair:" + pairs.getKey() + "=" + pairs.getValue());
            operators.add(pairs.getKey(), ReservedValues.RECORD_OPERATOR_ALL);
            query.add(pairs.getKey(), pairs.getValue());
          }
        }

        try {
          ServiceDaoMongoDb db = ServiceDaoMongoDb.getInstance();
          if (db != null) {
            Message res = db.queryAndPublishService(request, query, operators);
            response = new JSONRegisterResponse(res.getMap());
            String responseString = null;
            try {
              responseString = JSONMessage.toString(response);
            } catch (DataFormatException e) {

              LOG.fatal("Data formatting exception");
              LOG.info("Register status: FAILED due to Data formatting error; exiting");
              throw new InternalErrorException(
                  "Error in creating response. Data formatting exception at server.");
            }

            LOG.info("Register status: SUCCESS; exiting");
            LOG.debug("response:" + responseString);

            if (PublishService.isServiceOn()) {
              Publisher publisher = Publisher.getInstance();
              publisher.eventNotification(res);
            }

            return responseString;
          } else {
            throw new InternalErrorException("Cannot access database");
          }

        } catch (DuplicateEntryException e) {

          LOG.error("FobiddenRequestException:" + e.getMessage());
          LOG.info("Register status: FAILED due to Duplicate Entry; exiting");
          throw new ForbiddenRequestException(e.getMessage());

        } catch (DatabaseException e) {

          LOG.fatal("DatabaseException:" + e.getMessage());
          LOG.info("Register status: FAILED due to Database Exception; exiting");
          throw new InternalErrorException("Internal Server Error" + e.getMessage());
        }
      } else {

        // Build response
        LOG.fatal("Failed to secure lease for the registration record");
        LOG.info("Register status: FAILED; exiting");
        throw new ForbiddenRequestException("Failed to secure lease for the registration record");
      }

    } else {

      if (!this.isValid(request)) {

        LOG.error("Invalid request");
        LOG.info("Register status: FAILED due to Invalid Request; exiting");
        throw new BadRequestException("Invalid request. Please check the key-value pairs");

      } else if (!this.isAuthed(request)) {

        LOG.error("Not authorized to perform the request");
        LOG.info("Register status: FAILED; exiting");
        throw new UnauthorizedException("Not authorized to perform the request");
      }
    }

    return "\n";
  }

  private boolean isAuthed(JSONRegisterRequest request) {

    // The only case where a service registration is denied is when a service with the same name,
    // same type with
    // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot
    // be overwritten.
    // TODO: needs to be implemented
    return true;
  }

  private boolean isValid(JSONRegisterRequest request) {

    // Checks if ke "type" is present
    boolean res = request.validate();

    if (res) {

      String recordType = request.getRecordType();

      if ((recordType == null) || recordType.isEmpty()) {
        return false;
      }
    }

    return res;
  }

  private String newUri(String recordType) {

    if (recordType != null && !recordType.isEmpty()) {
      String uri =
          LookupService.SERVICE_URI_PREFIX + "/" + recordType + "/" + UUID.randomUUID().toString();
      return uri;
    } else {
      LOG.error("Error creating URI: Record Type not found!");
      throw new BadRequestException("Cannot create URI. Record Type not found.");
    }
  }

  private boolean isIgnoreKey(String key) {

    if (key.equals(ReservedKeys.RECORD_TTL)
        || key.equals(ReservedKeys.RECORD_EXPIRES)
        || key.equals(ReservedKeys.RECORD_URI)) {
      return true;
    } else {
      return false;
    }
  }
}
