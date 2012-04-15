package net.es.mp.scheduler.rest;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authz.AuthorizationException;
import net.es.mp.scheduler.MPSchedulingService;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.util.RESTAuthnUtil;
import net.sf.json.JSONObject;

@Path("/mp/schedules/{scheduleId}")
public class ScheduleResource {
    Logger log = Logger.getLogger(SchedulesResource.class);
    Logger netLogger = Logger.getLogger("netLogger");
    MPSchedulingService globals =  MPSchedulingService.getInstance();
    final private String GET_EVENT = "mp.scheduler.rest.ScheduleResource.get";
    final private String DELETE_EVENT = "mp.scheduler.rest.ScheduleResource.delete";
    final private String PUT_EVENT = "mp.scheduler.rest.ScheduleResource.update";
    @Context UriInfo uriInfo;
    
    @Produces("application/json")
    @GET
    public Response get(@PathParam("scheduleId") String scheduleId, @Context HttpHeaders httpHeaders){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.info(netLog.start(GET_EVENT));
        
        //authenticate
        AuthnSubject authnSubject = RESTAuthnUtil.extractAuthnSubject(httpHeaders, 
                globals.getContainer().getAuthnSubjectFactory());
        
        //call schedule manager
        Schedule schedule = null;
        try{
            schedule = globals.getManager().getSchedule(scheduleId, authnSubject);
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
        if(schedule == null){
            return Response.status(Status.NOT_FOUND).entity("Schedule resource not found").build();
        }
        
        //output JSON
        this.netLogger.info(netLog.end(GET_EVENT));
        return Response.ok().entity(schedule.toJSONString()).build();
    }
    
    @Produces("application/json")
    @DELETE
    public Response delete(@PathParam("scheduleId") String scheduleId, @Context HttpHeaders httpHeaders){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.info(netLog.start(DELETE_EVENT));
        
        //authenticate
        AuthnSubject authnSubject = RESTAuthnUtil.extractAuthnSubject(httpHeaders, 
                globals.getContainer().getAuthnSubjectFactory());
        
        //call schedule manager
        boolean validResource = false;
        try{
            validResource = globals.getManager().deleteSchedule(scheduleId, authnSubject);
        }catch(AuthorizationException e){
            this.netLogger.error(netLog.error(DELETE_EVENT, e.getMessage()));
            this.log.error(e.getMessage());
            e.printStackTrace();
            return Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build();
        }catch(Exception e){
            this.netLogger.error(netLog.error(DELETE_EVENT, e.getMessage()));
            this.log.error(e.getMessage());
            e.printStackTrace();
            return Response.serverError().entity(e.getMessage()).build();
        }
        
        //check for not found
        if(!validResource){
            return Response.status(Status.NOT_FOUND).entity("Schedule resource not found").build();
        }
        
        //build response
        JSONObject response = new JSONObject();
        response.put("uri", uriInfo.getRequestUri().toASCIIString());
        
        //output JSON
        this.netLogger.info(netLog.end(DELETE_EVENT));
        return Response.ok().entity(response.toString()).build();
    }
}
