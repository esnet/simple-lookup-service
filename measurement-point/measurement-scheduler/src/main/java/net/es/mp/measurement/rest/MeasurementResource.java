package net.es.mp.measurement.rest;

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
import net.es.mp.measurement.MPMeasurementService;
import net.es.mp.measurement.types.Measurement;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.util.RESTAuthnUtil;

import org.apache.log4j.Logger;

@Path("/mp/measurements/{measurementId}")
public class MeasurementResource {
    Logger log = Logger.getLogger(MeasurementResource.class);
    Logger netLogger = Logger.getLogger("netLogger");
    MPMeasurementService globals = MPMeasurementService.getInstance();
    final private String GET_EVENT = "mp.measurement.rest.MeasurementResource.get";
    @Context UriInfo uriInfo;
    
    @Produces("application/json")
    @GET
    public Response get(@PathParam("measurementId") String measurementId, @Context HttpHeaders httpHeaders){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.info(netLog.start(GET_EVENT));
        
        //authenticate
        AuthnSubject authnSubject = RESTAuthnUtil.extractAuthnSubject(httpHeaders, 
                globals.getContainer().getAuthnSubjectFactory());
        
        //call schedule manager
        Measurement measurement = null;
        try{
            measurement = globals.getManager().getMeasurement(measurementId, authnSubject);
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
        if(measurement == null){
            return Response.status(Status.NOT_FOUND).entity("Measurement resource not found").build();
        }
        
        //output JSON
        this.netLogger.info(netLog.end(GET_EVENT));
        return Response.ok().entity(measurement.toJSONString()).build();
    }
}
