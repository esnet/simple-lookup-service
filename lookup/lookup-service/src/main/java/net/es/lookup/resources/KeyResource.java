package net.es.lookup.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

/**
 *
 */
@Path("/lookup/service/{service}/{key}")
public class KeyResource {

    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String getService(@PathParam("service") String service, @PathParam("key") String key) {
        // Return some cliched textual content
        return "/lookup/services/" + service + "/" + key + "\n";
    }
}

