package net.es.lookup.resources;

import net.es.lookup.api.QueryServices;
import net.es.lookup.api.RegisterService;


import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */
@Path("/lookup/services")
public class ServicesResource {

    private QueryServices queryServices = new QueryServices();
    private RegisterService registerService = new RegisterService();


    // The Java method will process HTTP GET requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Consumes("application/json")
    @Produces("application/json")
    public String postHandler (String message) {
        return this.registerService.registerService(message);
    }


    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    // TODO: needs to implements parameters
    @Produces("application/json")
    public String getHandler () {
        return this.queryServices.query();
    }


}
