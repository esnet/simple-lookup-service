package net.es.lookup.resources;

import java.util.UUID;

import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONRegisterRequest;
import net.es.lookup.protocol.json.JSONRegisterResponse;
import net.es.lookup.service.LookupService;

/**
 *
 */
@Path("/lookup/service")
public class RegistrationResource {

    private String params;

    // The Java method will process HTTP GET requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Consumes("application/json")
    @Produces("application/json")
    public String registerService(String message) {
        // this.params = params;
        // Return some cliched textual content
        JSONRegisterResponse response;
        try {
            JSONRegisterRequest request = new JSONRegisterRequest(message);
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(request)) {
                // Generate a new URI for this service and add it to the service key/value pairs
                String uri = this.newURI ();
                request.add (Message.SERVICE_URI, uri);
                // Request a lease
                boolean gotLease = LeaseManager.getInstance().requestLease(request);
                if (gotLease) {
                    ServiceDAOMongoDb.getInstance().publishService(request);
                }

                // Build response
                response = new JSONRegisterResponse (request.getMap());
            }
        } catch (DuplicateKeyException e) {
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

        res = (((request.getAccessPoint()== null) || request.getAccessPoint().isEmpty()) ||
               (request.getTTL() == 0) ||
               ((request.getServiceType()== null) || request.getServiceType().isEmpty()));

        return res;
    }

    private String newURI() {
        String uri = LookupService.SERVICE_URI_PREFIX + "/" + UUID.randomUUID().toString();
        return uri;
    }

}

