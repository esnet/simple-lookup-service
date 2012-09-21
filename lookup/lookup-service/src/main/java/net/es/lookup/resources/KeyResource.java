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

    @GET
    @Produces("application/json")
    public String getHandler (@PathParam("service") String serviceid, @PathParam("key")String key){

    	String serviceuri = "lookup/service/"+serviceid;
        return this.accessService.getKeyService(serviceuri, key);

    }

}
