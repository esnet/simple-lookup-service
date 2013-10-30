package net.es.lookup.resources;


import net.es.lookup.api.AccessService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */

@Path("/{sls}/{record}/{recordid}/{key}")
public class KeyResource {

    private AccessService accessService = new AccessService();
    private String record_prefix = "lookup";

    @GET
    @Produces("application/json")
    public String getHandler(@PathParam("sls") String path, @PathParam("record") String record, @PathParam("recordid") String recordid, @PathParam("key") String key) {
        String dbname = path;
        String serviceuri = "lookup/"+ record+"/" + recordid;
        return this.accessService.getKeyService(dbname, serviceuri, key);

    }

}
