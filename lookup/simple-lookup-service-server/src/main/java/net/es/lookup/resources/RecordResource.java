package net.es.lookup.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import net.es.lookup.api.AccessService;
import net.es.lookup.api.EditService;
import net.es.lookup.common.exception.api.NotSupportedException;


/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */
@Path("/{sls}/{record}/{recordid}")
public class RecordResource {

  private EditService editService = new EditService();
  private AccessService accessService = new AccessService();
  private String dbname = "lookup";
  private String recordPrefix = dbname;

  /**
   * Get handler to retrieve record details.
   * **/
  @GET
  @Produces("application/json")
  public String getHandler(
      @PathParam("sls") String path,
      @PathParam("record") String record,
      @PathParam("recordid") String recordid) {
    String dbname = path;

    String serviceuri = path + "/" + record + "/" + recordid;
    return this.accessService.getService(serviceuri);
  }

  /**
   * Post handler to handle renewals.
   * */
  @POST
  @Produces("application/json")
  public String renewHandler(
      @PathParam("sls") String path,
      @PathParam("record") String record,
      @PathParam("recordid") String recordid,
      String message) {
    if (!path.equals(recordPrefix)) {
      throw new NotSupportedException("Operation not supported");
    }
    String serviceuri = path + "/" + record + "/" + recordid;
    return this.editService.renewService(serviceuri, message);
  }

  /**
   * This method handles record deletions.
   * **/
  @DELETE
  @Produces("application/json")
  public String deleteHandler(
      @PathParam("sls") String path,
      @PathParam("record") String record,
      @PathParam("recordid") String recordid,
      String service) {
    if (!path.equals(recordPrefix)) {
      throw new NotSupportedException("Operation not supported");
    }
    String serviceuri = path + "/" + record + "/" + recordid;
    return this.editService.deleteService(serviceuri, service);
  }
}
