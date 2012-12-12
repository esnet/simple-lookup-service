package net.es.lookup.resources;


import net.es.lookup.api.AccessService;

import javax.ws.rs.*;

/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */

@Path("/lookup/{record}/{recordid}")
public class AccessRecordResource {

    private AccessService accessService = new AccessService();

    @GET
    @Produces("application/json")
    public String getHandler(@PathParam("record") String record, @PathParam("recordid") String recordid) {

        String serviceuri = "lookup/"+ record +"/"+ recordid;
        return this.accessService.getService(serviceuri);

    }

    @DELETE
    @Produces("application/json")
    public String deleteHandler(@PathParam("record") String record, @PathParam("recordid") String recordid, String service) {

        String serviceuri = "lookup/"+ record + recordid;
        return this.accessService.deleteService(serviceuri, service);

    }

    @POST
    @Produces("application/json")
    public String renewHandler(@PathParam("record") String record, @PathParam("recordid") String recordid, String message) {

        String serviceuri = "lookup/"+ record + recordid;
        return this.accessService.renewService(serviceuri, message);

    }

}
