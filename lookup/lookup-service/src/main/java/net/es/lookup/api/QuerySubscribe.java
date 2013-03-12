package net.es.lookup.api;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.es.lookup.common.exception.internal.QueryException;
import net.es.lookup.common.exception.internal.QueueException;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONSubRequest;
import net.es.lookup.pubsub.amq.AMQueueManager;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;


public class QuerySubscribe {

    private String params;


    public String subscribe(String message) {


        String response;

        JSONSubRequest request = new JSONSubRequest(message);
        if (request.getStatus() == JSONSubRequest.INCORRECT_FORMAT) {
            System.out.println("INCORRECT FORMAT");
            // TODO: return correct error code
            return "402\n";
        }
        // Verify that request is valid and authorized
        if (this.isValid(request) && this.isAuthed(request)) {
            //List<Service> res = ServiceDAOMongoDb.getInstance().query(request);

            // Build response
            Message res = new Message();
            res.add("url", "http://localhost:61616");

            AMQueueManager amqmanager = AMQueueManager.getInstance();
            try {
                List<String> qlist = amqmanager.getQueues(request);

                res.add("qid", qlist);
                response = JSONMessage.toString(res);
            } catch (QueryException e) {
                throw new InternalErrorException(e.getMessage());
            } catch (QueueException e) {
                throw new InternalErrorException(e.getMessage());
            } catch (DataFormatException e) {
                throw new InternalErrorException(e.getMessage());
            }

            return response;
        }

        return "\n";




    }


    private boolean isAuthed(JSONSubRequest request) {

        // The only case where a service registration is denied is when a service with the same name, same type with
        // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot be overwritten.
        // TODO: needs to be implemented
        return true;

    }


    private boolean isValid(JSONSubRequest request) {

        // All mandatory key/value must be present
        boolean res = true;
        return res;

    }
}
