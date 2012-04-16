package net.es.mp.measurement.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authz.AuthorizationException;
import net.es.mp.measurement.MPMeasurementService;
import net.es.mp.measurement.types.Measurement;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.util.RESTAuthnUtil;

import org.apache.log4j.Logger;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Path("/mp/measurements")
public class MeasurementsResource {
    Logger log = Logger.getLogger(MeasurementResource.class);
    Logger netLogger = Logger.getLogger("netLogger");
    MPMeasurementService globals = MPMeasurementService.getInstance();
    final private String POST_EVENT = "mp.measurement.rest.MeasurementResource.post";
    @Context UriInfo uriInfo;
    
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public Response post(String body, @Context HttpHeaders httpHeaders){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.info(netLog.start(POST_EVENT));
        
        //parse input
        Measurement measurement =new Measurement((DBObject)JSON.parse(body));
        AuthnSubject authnSubject = RESTAuthnUtil.extractAuthnSubject(httpHeaders, 
                globals.getContainer().getAuthnSubjectFactory());
        
        //call stream manager
        try{
            globals.getManager().createMeasurement(measurement, uriInfo.getPath(), authnSubject);
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
        return Response.ok().entity(measurement.toJSONString()).build();
    }
}
