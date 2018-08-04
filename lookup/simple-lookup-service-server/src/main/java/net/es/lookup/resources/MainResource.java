package net.es.lookup.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import net.es.lookup.api.BulkRenewService;
import net.es.lookup.api.QueryServices;
import net.es.lookup.api.RegisterService;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.api.NotSupportedException;

/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */
@Path("/{sls}/records")
public class MainResource {

  private QueryServices queryServices = new QueryServices();
  private RegisterService registerService = new RegisterService();
  private BulkRenewService bulkRenewService = new BulkRenewService();
  private String prefix = "lookup";

  /** Post handler to register records. */
  @POST
  @Consumes("application/json")
  @Produces("application/json")
  public String postHandler(@PathParam("sls") String sls, String message) {

    if (sls.equalsIgnoreCase(prefix)) {
      return this.registerService.registerService(message);
    } else {
      throw new NotSupportedException("Register Operation not supported");
    }
  }

  /** Get handler to query records. */
  @GET
  @Produces("application/json")
  public String getHandler(@Context UriInfo ui, @PathParam("sls") String sls) {

    MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
    Message message = new Message();
    int maxResults = 0;
    int skip = 0;

    for (String key : queryParams.keySet()) {

      if (key.equals(ReservedKeys.RECORD_OPERATOR)) {

        List<String> ops = new ArrayList();
        ops.add(queryParams.getFirst(key));
        message.add(key, ops);

      } else if (key.equals(ReservedKeys.RECORD_SKIP)) {

        skip = Integer.parseInt(queryParams.getFirst(key));

      } else if (key.equals(ReservedKeys.RECORD_MAXRESULTS)) {

        maxResults = Integer.parseInt(queryParams.getFirst(key));

      } else {

        // Not skip, operator or max-results. Must be key/values pair for the query
        String[] strArr = queryParams.getFirst(key).split(",");
        if (strArr.length > 1) {

          message.add(key, Arrays.asList(strArr));

        } else {

          message.add(key, queryParams.getFirst(key));
        }
      }
    }

    return this.queryServices.query(message, maxResults);
  }

  /** Put handler for bulk renews of records. */
  @PUT
  @Consumes("application/json")
  @Produces("application/json")
  public String bulkRenewHandler(String message) {
    return bulkRenewService.bulkRenew(message);
  }
}
