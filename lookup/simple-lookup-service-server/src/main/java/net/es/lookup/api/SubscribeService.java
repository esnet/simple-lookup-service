package net.es.lookup.api;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotSupportedException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.PubSubQueryException;
import net.es.lookup.common.exception.internal.PubSubQueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONSubRequest;
import net.es.lookup.protocol.json.JSONSubResponse;
import net.es.lookup.pubsub.QueueServiceMapping;
import net.es.lookup.pubsub.amq.AMQueueManager;
import net.es.lookup.utils.config.reader.QueueServiceConfigReader;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


public class SubscribeService {

    private static Logger LOG = Logger.getLogger(SubscribeService.class);

    public String subscribe(String serviceName, String message) {

        QueueServiceConfigReader queueServiceConfigReader = QueueServiceConfigReader.getInstance();
        String response;

        LOG.info("net.es.lookup.api.SubscribeService.subscribe: Received request - " + message);
        JSONSubRequest request = new JSONSubRequest(message);
//        if (request.getStatus() == JSONSubRequest.INCORRECT_FORMAT) {
//            LOG.error("net.es.lookup.api.SubscribeService.subscribe: Incorrect JSON format ");
//            throw new BadRequestException("Error parsing request. Please check the key-value pairs.");
//        }
        // Verify that request is valid and authorized
        if (this.isValid(request) && this.isAuthed(request)) {
            // Build response
            LOG.debug("net.es.lookup.api.SubscribeService.subscribe: Valid request " + request.getMap());
            JSONSubResponse res = new JSONSubResponse();
            List<String> locator = new ArrayList<String>();
            locator.add(queueServiceConfigReader.getUrl());
            res.add(ReservedKeys.RECORD_SUBSCRIBE_LOCATOR, locator);

            AMQueueManager amqmanager = (AMQueueManager) QueueServiceMapping.getQueueManager(serviceName);
            try {
                List<String> qlist = amqmanager.getQueues(request);
                res.add(ReservedKeys.RECORD_SUBSCRIBE_QUEUE, qlist);
                LOG.info("net.es.lookup.api.SubscribeService.subscribe: Returning queues - " + res);
                response = JSONMessage.toString(res);
                LOG.info("net.es.lookup.api.SubscribeService.subscribe: Returning queues - " + response);
                return response;
            } catch (PubSubQueryException e) {
                throw new InternalErrorException(e.getMessage());
            }  catch (DataFormatException e) {
                throw new InternalErrorException(e.getMessage());
            }


        } else {
            LOG.error("net.es.lookup.api.SubscribeService.subscribe: Query contains > 1 key-value pairs ");
            throw new BadRequestException("Subscribe supports only queries with 0 or 1 key-value pairs");
        }


    }


    private boolean isAuthed(JSONSubRequest request) {

        // The only case where a service registration is denied is when a service with the same name, same type with
        // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot be overwritten.
        // TODO: needs to be implemented
        return true;

    }

    /**
     * Checks if the message is valid
     */
    private boolean isValid(JSONSubRequest request) {

        // Can contain 0 or 1 key-value pair
        boolean res = true;
        if (request.getMap().size() > 1) {
            res = false;
        }
        LOG.debug("net.es.lookup.api.SubscribeService.isValid: Query size - " + request.getMap().size());
        LOG.debug("net.es.lookup.api.SubscribeService.isValid: Return Value - " + res);
        return res;

    }
}
