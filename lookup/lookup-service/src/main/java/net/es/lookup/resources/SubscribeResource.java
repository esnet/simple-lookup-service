package net.es.lookup.resources;


import org.atmosphere.annotation.Broadcast;
import org.atmosphere.annotation.Suspend;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.cpr.BroadcasterFactory;
import org.atmosphere.jersey.Broadcastable;

import javax.ws.rs.*;


/**
 * This class and other similar resource classes need to be explicitly loaded in the
 * net.es.lookup.service.LookupService class
 */
@Path("/lookup/subscribe/{topic}")
@Produces("text/plain")

public class SubscribeResource {

    @GET
    @Suspend
    public Broadcastable receive(@HeaderParam("X-Remote-User") Broadcaster user) {

        return new Broadcastable(user);
    }

    @POST
    @Broadcast
    public Broadcastable send(@FormParam("to") String to,
                              @FormParam("msg") String msg) {

        Broadcaster user =
                BroadcasterFactory.getDefault().lookup(Broadcaster.class, to);
        return user == null ? null : new Broadcastable(msg, user);
    }

}
