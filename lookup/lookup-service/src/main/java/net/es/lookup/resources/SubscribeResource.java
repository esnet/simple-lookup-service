package net.es.lookup.resources;

import net.es.lookup.api.QuerySubscribe;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;


/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */
@Path("/lookup/subscribe")
@Produces("text/plain")

public class SubscribeResource {
    private QuerySubscribe qs = new QuerySubscribe();


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String getHandler(String message) {

    return qs.subscribe(message);


    }
}
