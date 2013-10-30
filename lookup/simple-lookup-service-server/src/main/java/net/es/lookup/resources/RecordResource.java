package net.es.lookup.resources;


import net.es.lookup.api.AccessService;
import net.es.lookup.api.EditService;
import net.es.lookup.common.exception.api.NotSupportedException;

import javax.ws.rs.*;

/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */

@Path("/{sls}/{record}/{recordid}")
public class RecordResource {

    private EditService editService = new EditService();
    private AccessService accessService = new AccessService();
    private String dbname = "lookup";
    private String record_prefix = dbname;

    @GET
    @Produces("application/json")

    public String getHandler(@PathParam("sls") String path, @PathParam("record") String record, @PathParam("recordid") String recordid) {
        String dbname = path;

        String serviceuri = record_prefix + record +"/"+ recordid;
        return this.accessService.getService(dbname,serviceuri);

    }


    @POST
    @Produces("application/json")
    public String renewHandler(@PathParam("sls") String path,@PathParam("record") String record, @PathParam("recordid") String recordid, String message) {
        if(!path.equals(record_prefix)){
            throw new NotSupportedException("Operation not supported");
        }
        String serviceuri = record_prefix + record +"/"+ recordid;
        return this.editService.renewService(dbname, serviceuri, message);

    }


    @DELETE
    @Produces("application/json")
    public String deleteHandler(@PathParam("sls") String path, @PathParam("record") String record, @PathParam("recordid") String recordid, String service) {
        if(!path.equals(record_prefix)){
            throw new NotSupportedException("Operation not supported");
        }
        String serviceuri = record_prefix + record +"/"+ recordid;
        return this.editService.deleteService(dbname, serviceuri, service);

    }

}
