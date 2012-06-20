package net.es.lookup.resources;


import net.es.lookup.api.DeleteService;
import net.es.lookup.api.AccessService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */
@Path("/lookup/service/{service}")
public class AccessServiceResource {

    private DeleteService deleteService = new DeleteService();
    private AccessService accessService = new AccessService();


    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String getHandler (@PathParam("service") String serviceid) {
    
        return this.accessService.getService(serviceid);
    }


    // The Java method will process HTTP GET requests
    @DELETE
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String deleteHandler (@PathParam("service") String serviceid, String service) {
//    	return this.deleteService.delete(serviceid);
    	String serviceuri = "service/"+serviceid;
    	return this.accessService.deleteService(serviceuri, service);
    }
    
    
    // The Java method will process HTTP POST requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String renewHandler (@PathParam("service") String serviceid, String message) {
    	String serviceuri = "service/"+serviceid;
        return this.accessService.renewService(serviceuri,message);
    }
}
