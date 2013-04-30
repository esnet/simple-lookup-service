package net.es.lookup.api;

import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.ReservedValues;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.ForbiddenRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.UnauthorizedException;
import net.es.lookup.common.exception.internal.*;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRegisterRequest;
import net.es.lookup.protocol.json.JSONRegisterResponse;
import net.es.lookup.pubsub.amq.AMQueuePump;
import net.es.lookup.service.LookupService;
import org.apache.log4j.Logger;

import java.util.*;

/**
 *
 */
public class RegisterService {

    private static Logger LOG = Logger.getLogger(RegisterService.class);
    private String params;

    public String registerService(String message) {

        LOG.info(" Processing registerService.");
        LOG.info(" Received message: " + message);
        JSONRegisterResponse response;
        JSONRegisterRequest request = new JSONRegisterRequest(message);

        if (request.getStatus() == JSONRegisterRequest.INCORRECT_FORMAT) {

            LOG.info("Register status: FAILED; exiting");
            LOG.error(" INCORRECT FORMAT OF JSON DATA");
            // TODO: return correct error code
            throw new BadRequestException("Error in JSON data");

        }

        LOG.debug("valid?" + this.isValid(request));

        if (this.isValid(request) && this.isAuthed(request)) {

            // Request a lease
            boolean gotLease = LeaseManager.getInstance().requestLease(request);

            if (gotLease) {

                List<String> recordType = request.getRecordType();

                // Generate a new URI for this service and add it to the service key/value pairs
                String rType = recordType.get(0);

                String uri = this.newURI(rType);
                request.add(ReservedKeys.RECORD_URI, uri);

                //Add the state
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

                    Message res = ServiceDAOMongoDb.getInstance().queryAndPublishService(request, query, operators);
                    response = new JSONRegisterResponse(res.getMap());
                    String responseString = JSONMessage.toString(response);

                    List<Message> resList = new ArrayList<Message>();
                    resList.add(res);
                    try {
                        AMQueuePump.getInstance().fillQueues(resList);
                    } catch (QueueException e) {
                        LOG.error("Error sending Register Record  to Queue");
                        LOG.info("Register: Caught Queue Exception");
                    } catch (QueryException e) {
                        LOG.error("Error sending Register Record  to Queue");
                        LOG.info("Register: Caught Query Exception");
                    }
                    LOG.info("Register status: SUCCESS; exiting");
                    LOG.debug("response:" + responseString);
                    return responseString;

                } catch (DataFormatException e) {

                    LOG.fatal("Data formating exception");
                    LOG.info("Register status: FAILED; exiting");
                    throw new InternalErrorException("Data formatting exception");

                } catch (DuplicateEntryException e) {

                    LOG.error("FobiddenRequestException:" + e.getMessage());
                    LOG.info("Register status: FAILED; exiting");
                    throw new ForbiddenRequestException(e.getMessage());

                } catch (DatabaseException e) {

                    LOG.fatal("DatabaseException:" + e.getMessage());
                    LOG.info("Register status: FAILED; exiting");
                    throw new InternalErrorException(e.getMessage());

                }
            } else {

                // Build response
                LOG.fatal("Failed to secure lease for the registration record");
                LOG.info("Register status: FAILED; exiting");
                throw new ForbiddenRequestException("Failed to secure lease for the registration record");

            }

        } else {

            if (!this.isValid(request)) {

                LOG.error("Invalid requestUrl:");
                LOG.info("Register status: FAILED; exiting");
                throw new BadRequestException("Invalid requestUrl");

            } else if (!this.isAuthed(request)) {

                LOG.error("Not authorized to perform the requestUrl");
                LOG.info("Register status: FAILED; exiting");
                throw new UnauthorizedException("Not authorized to perform the requestUrl");

            }

        }

        return "\n";

    }


    private boolean isAuthed(JSONRegisterRequest request) {

        // The only case where a service registration is denied is when a service with the same name, same type with
        // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot be overwritten.
        // TODO: needs to be implemented
        return true;

    }


    private boolean isValid(JSONRegisterRequest request) {

        // All mandatory key/value must be present
        boolean res = request.validate();

        if (res) {

            List<String> recordType = request.getRecordType();

            if ((recordType == null) || recordType.isEmpty()) {
                return false;
            }

            if (recordType.size() > 1) {
                return false;
            }

        }

        return res;

    }


    private String newURI(String rType) {

        if (rType != null && !rType.isEmpty()) {
            String uri = LookupService.SERVICE_URI_PREFIX + "/" + rType + "/" + UUID.randomUUID().toString();
            return uri;
        } else {
            LOG.error("Error creating URI: Record Type not found!");
            throw new BadRequestException("Cannot create URI. Record Type not found.");
        }

    }


    private boolean isIgnoreKey(String key) {

        if (key.equals(ReservedKeys.RECORD_TTL) || key.equals(ReservedKeys.RECORD_EXPIRES) || key.equals(ReservedKeys.RECORD_URI)) {
            return true;
        } else {
            return false;
        }

    }


}

