package net.es.lookup.api;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.api.BadRequestException;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.api.NotSupportedException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONSubRequest;
import net.es.lookup.protocol.json.JSONSubResponse;
import net.es.lookup.pubsub.amq.AMQueueManager;
import net.es.lookup.utils.QueueServiceConfigReader;
import java.util.ArrayList;
import java.util.List;


public class QuerySubscribe {

    private String params;


    public String subscribe(String message) {

        QueueServiceConfigReader queueServiceConfigReader = QueueServiceConfigReader.getInstance();
        String response;
        JSONSubRequest request = new JSONSubRequest(message);
        if (request.getStatus() == JSONSubRequest.INCORRECT_FORMAT) {
            System.out.println("INCORRECT FORMAT");
            // TODO: return correct error code
            throw new BadRequestException("Error parsing requestUrl");
        }
        if(!queueServiceConfigReader.isServiceUp()){
            throw new NotSupportedException("Queue ServiceRecord Not Supported");
        }
        // Verify that requestUrl is valid and authorized
        if (this.isValid(request) && this.isAuthed(request)) {
            // Build response

            JSONSubResponse res = new JSONSubResponse();
            List<String> locator = new ArrayList<String>();
            locator.add(queueServiceConfigReader.getUrl());
            res.add(ReservedKeys.RECORD_SUBSCRIBE_LOCATOR, locator);

            AMQueueManager amqmanager = AMQueueManager.getInstance();
            try {
                List<String> qlist = amqmanager.getQueues(request);

                res.add(ReservedKeys.RECORD_SUBSCRIBE_QUEUE, qlist);
                response = JSONMessage.toString(res);
            } catch (QueryException e) {
                throw new InternalErrorException(e.getMessage());
            } catch (QueueException e) {
                throw new InternalErrorException(e.getMessage());
            } catch (DataFormatException e) {
                throw new InternalErrorException(e.getMessage());
            }

            return response;
        }else{
            throw new BadRequestException("Subscribe supports only empty queries");
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
     * */
    private boolean isValid(JSONSubRequest request) {

        // Should be an empty query
        boolean res = true;
        if(request.getMap().size()<1  ) {
            res = false;
        }
        System.out.println("Query size: "+ request.getMap().size());
        System.out.println("Return value: "+ res);
        return res;

    }
}
