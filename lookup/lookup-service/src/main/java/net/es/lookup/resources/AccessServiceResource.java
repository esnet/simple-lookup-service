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

    @GET
    @Produces("application/json")
    public String getHandler (@PathParam("service") String serviceid) {

    	String serviceuri = "lookup/service/"+serviceid;
        return this.accessService.getService(serviceuri);

    }

    @DELETE
    @Produces("application/json")
    public String deleteHandler (@PathParam("service") String serviceid, String service) {

    	String serviceuri = "lookup/service/"+serviceid;
    	return this.accessService.deleteService(serviceuri, service);

    }

    @POST
    @Produces("application/json")
    public String renewHandler (@PathParam("service") String serviceid, String message) {

    	String serviceuri = "lookup/service/"+serviceid;
        return this.accessService.renewService(serviceuri,message);

    }

}
