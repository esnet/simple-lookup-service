package net.es.lookup.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Service;
import net.es.lookup.common.Message;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.protocol.json.JSONQueryRequest;
import net.es.lookup.protocol.json.JSONQueryResponse;

@Path("/lookup/query")
public class QueryResource {


    private String params;

    // The Java method will process HTTP GET requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Consumes("application/json")
    @Produces("application/json")
    public String query(String message) {
        // this.params = params;
        // Return some cliched textual content
        String response;
        try {
            JSONQueryRequest request = new JSONQueryRequest(message);
            if (request.getStatus() == JSONQueryRequest.INCORRECT_FORMAT) {
                System.out.println("INCORRECT FORMAT");
                // TODO: return correct error code
                return "402\n";
            }
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(request)) {
                List<Service> res = ServiceDAOMongoDb.getInstance().query(request);

                // Build response
                response = JSONMessage.toString(res);
                return response;
            }
        } catch (DuplicateKeyException e) {
            Thread.dumpStack();
            // TODO: Handle error
        }
        return "\n";
    }

    private boolean isAuthed(JSONQueryRequest request) {
        // The only case where a service registration is denied is when a service with the same name, same type with
        // the same client-uuid: this ensures that a service entry with a specified client-uuid cannot be overwritten.
        // TODO: needs to be implemented
        return true;
    }


    private boolean isValid(JSONQueryRequest request) {
        // All mandatory key/value must be present
        boolean res = true;
        return res;
    }

}
