package net.es.lookup.resources;

import net.es.lookup.api.QueryServices;
import net.es.lookup.api.RegisterService;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeywords;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */

@Path("/lookup/records")
public class RecordResource {

    private QueryServices queryServices = new QueryServices();
    private RegisterService registerService = new RegisterService();


    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public String postHandler(String message) {

        return this.registerService.registerService(message);

    }


    @GET
    @Produces("application/json")
    public String getHandler(@Context UriInfo ui) {

        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        Message message = new Message();
        int maxResults = 0;
        int skip = 0;

        for (String key : queryParams.keySet()) {

            if (key.equals(ReservedKeywords.RECORD_OPERATOR)) {

                List<String> ops = new ArrayList();
                ops.add(queryParams.getFirst(key));
                message.add(key, ops);

            } else if (key.equals(ReservedKeywords.RECORD_SKIP)) {

                skip = Integer.parseInt(queryParams.getFirst(key));

            } else if (key.equals(ReservedKeywords.RECORD_MAXRESULTS)) {

                maxResults = Integer.parseInt(queryParams.getFirst(key));

            } else {

                // Not skip, operator or max-results. Must be key/values pair for the query
                String strArr[] = queryParams.getFirst(key).split(",");
                if (strArr.length > 1) {

                    message.add(key, Arrays.asList(strArr));

                } else {

                    message.add(key, queryParams.getFirst(key));

                }

            }

        }

        return this.queryServices.query(message, maxResults, skip);

    }


}
