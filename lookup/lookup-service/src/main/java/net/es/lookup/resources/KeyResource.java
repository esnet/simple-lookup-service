package net.es.lookup.resources;


import net.es.lookup.api.AccessService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * This class and other similar resource classes need to be explicitly loaded in the 
 * net.es.lookup.service.LookupService class
 */
@Path("/lookup/service/{service}/{key}")
public class KeyResource {

    private AccessService accessService = new AccessService();


    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String getHandler (@PathParam("service") String serviceid, String service,@PathParam("key")String key) {
    	String serviceuri = "lookup/service/"+serviceid;
        return this.accessService.getKeyService(serviceuri,service,key);
    }


    
}
