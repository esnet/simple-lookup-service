package net.es.lookup.resources;


import net.es.lookup.api.DeleteService;
import net.es.lookup.api.AccessService;

import javax.ws.rs.GET;
import javax.ws.rs.DELETE;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 *
 */
@Path("/lookup/services/{service}")
public class AccessServiceResource {

    private DeleteService deleteService = new DeleteService();
    private AccessService accessService = new AccessService();


    // The Java method will process HTTP GET requests
    @GET
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String getHandler (@PathParam("service") String serviceId) {

        return this.accessService.getService(serviceId);
    }


    // The Java method will process HTTP GET requests
    @DELETE
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Produces("application/json")
    public String deleteHandler (@PathParam("service") String serviceid) {
        return this.deleteService.delete(serviceid);
    }
}
