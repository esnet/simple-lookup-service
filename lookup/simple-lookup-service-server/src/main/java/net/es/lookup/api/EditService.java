package net.es.lookup.api;

import java.util.ArrayList;
import java.util.Map;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotFoundException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.common.exception.internal.RecordNotFoundException;
import net.es.lookup.database.ServiceDaoMongoDb;
import net.es.lookup.protocol.json.JSONDeleteRequest;
import net.es.lookup.protocol.json.JSONDeleteResponse;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRenewRequest;
import net.es.lookup.protocol.json.JSONRenewResponse;
import net.es.lookup.publish.Publisher;
import net.es.lookup.service.PublishService;
import org.apache.log4j.Logger;

/** Author: sowmya. Date: 10/29/13 Time: 3:24 PM */
public class EditService {

  private static Logger LOG = Logger.getLogger(AccessService.class);

  /**
   * Method to renew record.
   * @param serviceid id of the record to delete
   * @param service record to be deleted
   * @return Strimg Json message of the deleted record expressed as string.
   * */
  public String renewService(String serviceid, String service) {

    LOG.info("Processing renewService...");
    LOG.info(" serviceid: " + serviceid);

    JSONRenewResponse response;
    Message errorResponse = new Message();
    JSONRenewRequest request = new JSONRenewRequest(service);

    // renew can be empty for now. next version will require the privatekey
    if (!service.isEmpty() && request.getStatus() == JSONRenewRequest.INCORRECT_FORMAT) {

      LOG.error(
          "requestStatus:" + request.getStatus() + " ServiceRecord requestUrl format is Incorrect");
      LOG.info("RenewService status: FAILED; exiting");
      throw new BadRequestException("ServiceRecord requestUrl format is Incorrect\n");
    }

    // Verify that requestUrl is valid and authorized
    LOG.debug("Is the requestUrl valid?" + this.isValid(request));

    if (this.isValid(request) && this.isAuthed(serviceid, request)) {

      try {
        ServiceDaoMongoDb db = ServiceDaoMongoDb.getInstance();
        if (db == null) {
          LOG.error(("Error accessing database object"));
          throw new InternalErrorException("Error accessing database");
        }
        Message serviceRecord = db.getRecordByUri(serviceid);

        if (serviceRecord != null) {

          LOG.debug("servicerecord not null");
          Map<String, Object> serviceMap = serviceRecord.getMap();

          if (request.getTTL() != null && !request.getTTL().isEmpty()) {

            serviceMap.put(ReservedKeys.RECORD_TTL, request.getTTL());

          } else {

            serviceMap.put(ReservedKeys.RECORD_TTL, new ArrayList());
          }

          Message newRequest = new Message(serviceMap);
          boolean gotLease = LeaseManager.getInstance().requestLease(newRequest);

          if (gotLease) {

            LOG.debug("gotLease for " + serviceid);
            // update state

            newRequest.add(ReservedKeys.RECORD_STATE, ReservedValues.RECORD_VALUE_STATE_RENEW);
            Message res = db.updateService(serviceid, newRequest);

            if (PublishService.isServiceOn()) {
              Publisher publisher = Publisher.getInstance();
              publisher.eventNotification(res);
            }
            response = new JSONRenewResponse(res.getMap());

            try {
              return JSONMessage.toString(response);

            } catch (DataFormatException e) {

              LOG.error("Data formatting exception");
              LOG.info("RenewService status: FAILED; exiting");
              throw new InternalErrorException("Data formatting exception");
            }

          } else {

            LOG.fatal("Failed to secure lease for the renew record");
            LOG.info("Renew status: FAILED; exiting");
            throw new ForbiddenRequestException("Failed to secure lease for the renewal record");
          }

        } else {

          LOG.error("ServiceRecord Not Found in DB.");
          LOG.info("RenewService status: FAILED; exiting");
          throw new NotFoundException("ServiceRecord Not Found in DB\n");
        }

      } catch (DatabaseException e) {

        LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
        LOG.info("RenewService status: FAILED; exiting");
        throw new InternalErrorException("Database error\n");
      }

    } else {

      if (!this.isValid(request)) {

        LOG.error("ServiceRecord Request is invalid");
        LOG.info("RenewService status: FAILED; exiting");
        throw new BadRequestException("Request is invalid\n");

      } else if (!this.isAuthed(serviceid, request)) {

        LOG.error("The private-key is not authorized to access this service");
        LOG.info("RenewService status: FAILED; exiting");
        throw new ForbiddenRequestException(
            "The private-key is not authorized to access this service\n");
      }

      try {

        LOG.info("RenewService status: SUCCESS");
        return JSONMessage.toString(errorResponse);

      } catch (DataFormatException e) {

        LOG.error("Data formatting exception");
        LOG.info("RenewService status: FAILED; exiting");
        throw new InternalErrorException("Data formatting exception");
      }
    }
  }

  /**
   * Method to delete record.
   * @param serviceid id of the record to delete
   * @param service record to be deleted
   * @return Strimg Json message of the deleted record expressed as string.
   * */
  public String deleteService(String serviceid, String service) {

    LOG.info("Processing deleteRecord...");
    LOG.info("serviceid: " + serviceid);
    JSONDeleteResponse response;

    JSONDeleteRequest request = new JSONDeleteRequest(service);

    if (request.getStatus() == JSONDeleteRequest.INCORRECT_FORMAT) {

      LOG.debug("INCORRECT FORMAT");
      LOG.error(
          "requestStatus:" + request.getStatus() + " ServiceRecord requestUrl format is Incorrect");
      LOG.info("DeleteService status: FAILED; exiting");
      throw new BadRequestException("ServiceRecord requestUrl format is Incorrect\n");
    }

    // Verify that requestUrl is valid and authorized
    LOG.debug("Is the requestUrl valid?" + this.isValid(request));

    if (this.isValid(request) && this.isAuthed(serviceid, request)) {
      try {
        ServiceDaoMongoDb db = ServiceDaoMongoDb.getInstance();

        if (db == null) {
          LOG.error("Error accessing database");
          throw new InternalErrorException("Error accessing database");
        }
        Message serviceRecord = db.deleteRecord(serviceid);

        if (serviceRecord == null) {

          LOG.error("ServiceRecord Not found");
          LOG.info("DeleteService status: FAILED; exiting");
          throw new NotFoundException("ServiceRecord not found in DB\n");

        } else {
          // update state
          serviceRecord.add(ReservedKeys.RECORD_STATE, ReservedValues.RECORD_VALUE_STATE_DELETE);

          response = new JSONDeleteResponse(serviceRecord.getMap());

          try {

            LOG.info("ServiceRecord Deleted");
            LOG.info("DeleteService status: SUCCESS; exiting");
            return JSONMessage.toString(response);

          } catch (DataFormatException e) {

            LOG.error("Data formatting exception");
            LOG.info("DeleteService status: FAILED; exiting");
            throw new InternalErrorException("Database error\n");
          }
        }

      } catch (DatabaseException e) {

        LOG.fatal("DatabaseException: The database is out of service." + e.getMessage());
        LOG.info("DeleteService status: FAILED; exiting");
        throw new InternalErrorException("Database error\n");

      } catch (RecordNotFoundException e) {

        LOG.info("Record Not found. DeleteService status: FAILED; exiting");
        throw new NotFoundException("Record does not exist");
      }
    } else {
      // fails either because it is not valid or not authorized
      if (!this.isValid(request)) {

        LOG.error("ServiceRecord Request is invalid");
        LOG.info("DeleteService status: FAILED; exiting");
        throw new BadRequestException("ServiceRecord Request is invalid\n");

      } else {

        LOG.error("The private-key is not authorized to access this service");
        LOG.info("DeleteService status: FAILED; exiting");
        throw new ForbiddenRequestException(
            "The private-key is not authorized to access this service\n");
      }
    }
  }

  private boolean isAuthed(String serviceid, JSONRenewRequest request) {

    // TODO: needs to be implemented. Check if client uuid matches
    return true;
  }

  private boolean isAuthed(String serviceid, JSONDeleteRequest request) {

    // TODO: needs to be implemented. Check if client uuid matches
    boolean res = request.validate();
    return res;
  }

  private boolean isValid(JSONRenewRequest request) {

    // TODO: add privatekey as mandatory key-value
    LOG.debug("Request's TTL= " + request.getTTL());
    boolean res;

    if (request != null) {

      res = ((request.validate()));

    } else {

      // can be empty for renew
      res = true;
    }

    return res;
  }

  private boolean isValid(JSONDeleteRequest request) {

    // TODO: needs to be implemented. Check for client-uuid
    return true;
  }
}
