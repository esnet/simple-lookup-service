package net.es.mp.scheduler.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import net.es.mp.authn.AuthnNotSupportedException;
import net.es.mp.authn.AuthnSubject;
import net.es.mp.authn.HttpsAuthenticator;
import net.es.mp.authn.UnableToAuthenticateException;
import net.es.mp.authz.AuthorizationException;
import net.es.mp.scheduler.MPSchedulingService;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.util.RESTAuthnUtil;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

@Path("/mp/schedules")
public class SchedulesResource {
    Logger log = Logger.getLogger(SchedulesResource.class);
    Logger netLogger = Logger.getLogger("netLogger");
    MPSchedulingService globals =  MPSchedulingService.getInstance();
    
    final private String POST_EVENT = "mp.scheduler.rest.SchedulesResource.post";
    final private String GET_EVENT = "mp.scheduler.rest.SchedulesResource.get";
    @Context UriInfo uriInfo;
    
    
    
    @Produces("application/json")
    @Consumes("application/json")
    @POST
    public Response post(String body, @Context HttpHeaders httpHeaders){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.info(netLog.start(POST_EVENT));
        
        //parse input
        Schedule schedule =new Schedule((DBObject)JSON.parse(body));
        AuthnSubject authnSubject = RESTAuthnUtil.extractAuthnSubject(httpHeaders, 
                globals.getContainer().getAuthnSubjectFactory());
        
        //call schedule manager
        try{
            MPSchedulingService.getInstance().getManager().createSchedule(schedule, uriInfo.getPath(), authnSubject);
        }catch(AuthorizationException e){
            this.netLogger.error(netLog.error(GET_EVENT, e.getMessage()));
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
        return Response.ok().entity(schedule.toJSONString()).build();
    }
    
    @Produces("application/json")
    @GET
    public String get(@Context HttpHeaders headers){
        AuthnSubject user = null;
        try {
            user = MPSchedulingService.getInstance().getContainer().getAuthnSubjectFactory().create(headers);
        } catch (AuthnNotSupportedException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        } catch (UnableToAuthenticateException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        JSONObject json = new JSONObject();
        json.put("subjectDN", user.getName());
        json.put("subjectIssuer", user.getAttributes().get(HttpsAuthenticator.ATTR_ISSUE_DN));

        return json.toString();
    }
}
