package net.es.lookup.api;

import java.util.ArrayList;
import java.util.UUID;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONRegisterRequest;
import net.es.lookup.protocol.json.JSONRegisterResponse;
import net.es.lookup.service.LookupService;

/**
 *
 */
public class RegisterService {

    private String params;

    public String registerService(String message) {
    	
        // this.params = params;
        // Return some cliched textual content
        JSONRegisterResponse response;
        try {
            JSONRegisterRequest request = new JSONRegisterRequest(message);
            if (request.getStatus() == JSONRegisterRequest.INCORRECT_FORMAT) {
                System.out.println("INCORRECT FORMAT");
                // TODO: return correct error code
                return "402\n";
            }
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(request)) {
                // Generate a new URI for this service and add it to the service key/value pairs
                String uri = this.newURI (); 
                request.add (Message.SERVICE_URI, uri);
                // Request a lease
                boolean gotLease = LeaseManager.getInstance().requestLease(request);
                if (gotLease) {
                    // Build the matching query request that must fail for the service to be published
                    Message query = new Message();
                    ArrayList<String> list = new ArrayList<String>();
                    list.add(request.getAccessPoint());
                    query.add(Message.ACCESS_POINT,list);
                    list = new ArrayList<String>();
                    list.add(request.getClientUUID());
                    query.add(Message.CLIENT_UUID,list);
                    list = new ArrayList<String>();
                    list.add(request.getServiceType());
                    query.add(Message.SERVICE_TYPE,list);
                    list = new ArrayList<String>();
                    list.add(request.getServiceDomain());
                    query.add(Message.SERVICE_DOMAIN,list);
                    System.out.println(request.getServiceDomain());

                    Message res = ServiceDAOMongoDb.getInstance().queryAndPublishService(request,query);

                    response = new JSONRegisterResponse (res.getMap());
                    return JSONMessage.toString(response);
                }

                // Build response
                response = new JSONRegisterResponse (request.getMap());
            }
        } catch (DuplicateKeyException e) {
            Thread.dumpStack();
            // TODO: Handle error
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
        boolean res = false;

        res = ! (((request.getAccessPoint()== null) || request.getAccessPoint().isEmpty()) ||
               (request.getTTL() == 0) ||
               ((request.getServiceType()== null) || request.getServiceType().isEmpty()));

        return res;
    }

    private String newURI() {
        String uri = LookupService.SERVICE_URI_PREFIX + "/" + UUID.randomUUID().toString();
        return uri;
    }

}

