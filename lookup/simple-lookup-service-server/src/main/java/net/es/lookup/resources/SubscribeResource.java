package net.es.lookup.resources;

import net.es.lookup.api.SubscribeService;

import javax.ws.rs.*;


/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */
@Path("/{sls}/subscribe")
@Produces("text/plain")

public class SubscribeResource {
    private SubscribeService qs = new SubscribeService();


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String getHandler(@PathParam("sls") String path, String message) {

    return qs.subscribe(path, message);


    }
}
