package net.es.lookup.resources;

import java.util.List;

import javax.ws.rs.Path;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.PathParam;

import net.es.lookup.common.Message;
import net.es.lookup.service.LookupService;
import net.es.lookup.protocol.json.JSONDeleteRequest;
import net.es.lookup.protocol.json.JSONDeleteResponse;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;
import net.es.lookup.common.DuplicateKeyException;



@Path("/lookup/service/delete/{service}")
public class DeleteResource{
	private String params;
	
    // The Java method will process HTTP DELETE requests
    @DELETE
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Consumes("application/json")
    @Produces("application/json")
    public String delete(String message, @PathParam("service") String serviceid) {
    	
        // this.params = params;
        // Return some cliched textual content
    	JSONDeleteResponse response;
        try {
            JSONDeleteRequest request = new JSONDeleteRequest(message);
            
            if (request.getStatus() == JSONDeleteRequest.INCORRECT_FORMAT) {
                System.out.println("INCORRECT FORMAT");
                // TODO: return correct error code
                return "402\n";
            }
            // Verify that request is valid and authorized
            if (this.isValid(request) && this.isAuthed(request)) {
            	System.out.println("came here");
                String serviceuri = "service/"+serviceid;
                request.add (Message.SERVICE_URI, serviceuri);
                
                Message res = ServiceDAOMongoDb.getInstance().deleteService(request);

                // Build response
                response = new JSONDeleteResponse(res.getMap());
                
                
                return JSONMessage.toString(response);
            }
        } catch (DuplicateKeyException e) {
            Thread.dumpStack();
            // TODO: Handle error
        }
        return "\n";
    }

    private boolean isAuthed(JSONDeleteRequest request) {
        // The only case where a service registration is denied is when client-uuid does not match: this ensures that a service entry with a specified client-uuid cannot be deleted accidentally.
        // TODO: needs to be implemented
        return true;
    }


    private boolean isValid(JSONDeleteRequest request) {
        // All mandatory key/value must be present
        boolean res = true;
        return res;
    }
}