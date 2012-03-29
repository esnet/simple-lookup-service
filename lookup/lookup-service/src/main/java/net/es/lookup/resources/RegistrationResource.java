package net.es.lookup.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.QueryParam;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONRegisterRequest;
import net.es.lookup.protocol.json.JSONRegisterResponse;

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
        try {
            JSONRegisterRequest request = new JSONRegisterRequest(message);
            ServiceDAOMongoDb.getInstance().publishService(request);
            JSONRegisterResponse response = new JSONRegisterResponse();
        } catch (DuplicateKeyException e) {
            // TODO: Handle error
        }
        return "\n";
    }
}

