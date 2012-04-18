package net.es.lookup.resources;

import javax.ws.rs.POST;
import javax.ws.rs.GET;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import net.es.lookup.api.QueryServices;
import net.es.lookup.api.RegisterService;
import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Message;

/**
 *
 */
@Path("/lookup/services")
public class ServicesResource {

    public static final String OPERATOR_ALL = "ALL";
    public static final String OPERATOR_ANY = "ANY";
    public static final String DEFAULT_OPERATOR = ServicesResource.OPERATOR_ALL;
    public static final String OPERATOR = "operator";
    public static final String SKIP = "skip";
    public static final String MAX_RESULTS = "max-results";

    private QueryServices queryServices = new QueryServices();
    private RegisterService registerService = new RegisterService();


    // The Java method will process HTTP GET requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"

    @Consumes("application/json")
    @Produces("application/json")
    public String postHandler (String message) {
        return this.registerService.registerService(message);
    }


    // The Java method will process HTTP GET requests
    @GET
    @Produces("application/json")
    public String getHandler (@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        Message message = new Message();
        boolean gotOperator = false;
        int maxResults = 0;
        int skip = 0;

        try {
            for (String key : queryParams.keySet()) {
                if (key.equals(ServicesResource.OPERATOR)) {
                    message.add (key, queryParams.getFirst(key));
                    gotOperator = true;
                } else if (key.equals(ServicesResource.SKIP)) {
                    skip = Integer.parseInt(queryParams.getFirst(key));
                } else if (key.equals(ServicesResource.MAX_RESULTS)) {
                    maxResults = Integer.parseInt(queryParams.getFirst(key));
                } else {
                    // Not skip, operator or max-results. Must be a key/value pair for the query
                    message.add (key, queryParams.getFirst(key));
                }
            }
        } catch (DuplicateKeyException e) {
            e.printStackTrace(); // TODO: needs error handling
        }

        return this.queryServices.query(message, maxResults, skip);
    }


}
