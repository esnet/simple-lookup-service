package net.es.lookup.resources;

import javax.ws.rs.*;


/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */
@Path("/lookup/subscribe/{topic}")
@Produces("text/plain")

public class SubscribeResource {

}
