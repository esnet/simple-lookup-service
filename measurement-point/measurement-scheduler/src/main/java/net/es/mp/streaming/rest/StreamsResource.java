package net.es.mp.streaming.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authz.AuthorizationException;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.streaming.MPStreamingService;
import net.es.mp.streaming.types.Stream;
import net.es.mp.util.RESTAuthnUtil;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Path("/mp/streams")
public class StreamsResource {
    Logger log = Logger.getLogger(StreamResource.class);
    Logger netLogger = Logger.getLogger("netLogger");
    MPStreamingService globals = MPStreamingService.getInstance();
    final private String POST_EVENT = "mp.streaming.rest.StreamResource.post";
    @Context UriInfo uriInfo;
    
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public Response post(String body, @Context HttpHeaders httpHeaders){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.info(netLog.start(POST_EVENT));
        
        //parse input
        Stream stream =new Stream((DBObject)JSON.parse(body));
        AuthnSubject authnSubject = RESTAuthnUtil.extractAuthnSubject(httpHeaders, 
                globals.getContainer().getAuthnSubjectFactory());
        
        //call stream manager
        try{
            globals.getManager().createStream(stream, uriInfo.getPath(), authnSubject);
        }catch(AuthorizationException e){
            this.netLogger.error(netLog.error(POST_EVENT, e.getMessage()));
            this.log.error(e.getMessage());
            e.printStackTrace();
            return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }catch(Exception e){
            this.netLogger.error(netLog.error(POST_EVENT, e.getMessage()));
            this.log.error(e.getMessage());
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
        
        //output JSON
        this.netLogger.info(netLog.end(POST_EVENT));
        return Response.ok().entity(stream.toJSONString()).build();
    }
}
