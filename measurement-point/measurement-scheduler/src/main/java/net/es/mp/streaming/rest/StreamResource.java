package net.es.mp.streaming.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authz.AuthorizationException;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.streaming.MPStreamingService;
import net.es.mp.streaming.types.Stream;
import net.es.mp.util.RESTAuthnUtil;

import org.apache.log4j.Logger;

@Path("/mp/streams/{streamId}")
public class StreamResource {
    Logger log = Logger.getLogger(StreamResource.class);
    Logger netLogger = Logger.getLogger("netLogger");
    MPStreamingService globals = MPStreamingService.getInstance();
    final private String GET_EVENT = "mp.streaming.rest.StreamResource.get";
    @Context UriInfo uriInfo;
    
    @Produces("application/json")
    @GET
    public Response get(@PathParam("streamId") String streamId, @Context HttpHeaders httpHeaders){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.info(netLog.start(GET_EVENT));
        
        //authenticate
        AuthnSubject authnSubject = RESTAuthnUtil.extractAuthnSubject(httpHeaders, 
                globals.getContainer().getAuthnSubjectFactory());
        
        //call schedule manager
        Stream stream = null;
        try{
            stream = globals.getManager().getStream(streamId, authnSubject);
        }catch(AuthorizationException e){
            this.netLogger.error(netLog.error(GET_EVENT, e.getMessage()));
            this.log.error(e.getMessage());
            e.printStackTrace();
            return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }catch(Exception e){
            this.netLogger.error(netLog.error(GET_EVENT, e.getMessage()));
            this.log.error(e.getMessage());
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
        
        //check for not found
        if(stream == null){
            return Response.status(Status.NOT_FOUND).entity("Stream resource not found").build();
        }
        
        //output JSON
        this.netLogger.info(netLog.end(GET_EVENT));
        return Response.ok().entity(stream.toJSONString()).build();
    }
}
