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
@Path("/lookup/service/{service}")
public class AccessServiceResource {

    private AccessService accessService = new AccessService();


    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String getHandler (@PathParam("service") String serviceid, String service) {
    	String serviceuri = "lookup/service/"+serviceid;
        return this.accessService.getService(serviceuri,service);
    }


    // The Java method will process HTTP GET requests
    @DELETE
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String deleteHandler (@PathParam("service") String serviceid, String service) {
    	String serviceuri = "lookup/service/"+serviceid;
    	return this.accessService.deleteService(serviceuri, service);
    }
    
    
    // The Java method will process HTTP POST requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String renewHandler (@PathParam("service") String serviceid, String message) {
    	String serviceuri = "lookup/service/"+serviceid;
        return this.accessService.renewService(serviceuri,message);
    }
}
