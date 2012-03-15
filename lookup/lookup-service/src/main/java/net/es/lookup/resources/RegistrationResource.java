package net.es.lookup.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

/**
 *
 */
@Path("/lookup/service")
public class RegistrationResource {

    // The Java method will process HTTP GET requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String getService() {
        // Return some cliched textual content
        return "/lookup/service/\n";
    }
}

