package net.es.mp.scheduler;

import java.net.URI;
import java.util.HashMap;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authz.AuthorizationException;
import net.es.mp.authz.AuthzAction;
import net.es.mp.authz.AuthzConditions;
import net.es.mp.scheduler.jobs.JobRouter;
import net.es.mp.scheduler.jobs.MPJobScheduler;
import net.es.mp.scheduler.types.Schedule;
import net.es.mp.util.IDUtil;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class ScheduleManager {
    Logger log = Logger.getLogger(ScheduleManager.class);
    Logger netLogger = Logger.getLogger("netLogger");
    JobRouter jobRouter;
    
    final private String SCHEDULE_COLLECTION = "schedules";
    
    final private String CREATE_EVENT = "mp.scheduler.ScheduleManager.createSchedule";
    final private String GET_EVENT = "mp.scheduler.ScheduleManager.getSchedule";
    final private String DELETE_EVENT = "mp.scheduler.ScheduleManager.deleteSchedule";
    final private String UPDATE_EVENT = "mp.scheduler.ScheduleManager.updateSchedule";
    
    public ScheduleManager(HashMap<String, MPJobScheduler> jobRoutingTable){
        this.jobRouter = new JobRouter(jobRoutingTable);
    }
   
    public void createSchedule(Schedule schedule, String uriPath, AuthnSubject authnSubject) throws MPSchedulerException, AuthorizationException{
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(CREATE_EVENT));
        
        //authorize ability to create at all 
        AuthzConditions authzConditions = MPSchedulingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.CREATE, null);
        
        //generate ID and uri
        String baseURI = MPSchedulingService.getInstance().getContainer().getResourceURL();
        ObjectId id = new ObjectId();
        String uri = IDUtil.generateURI(baseURI, uriPath, id.toString());
        schedule.setID(id);
        schedule.setURI(uri);
        
        //call JobRouter
        URI streamURI = this.jobRouter.submit(schedule, authzConditions);
        schedule.setStreamURI(streamURI.toASCIIString());
        
        //authorize validated resource
        authzConditions = MPSchedulingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.CREATE, schedule);
        
        this.netLogger.debug(netLog.start(CREATE_EVENT+".store"));
        //store in DB
        DB db = MPSchedulingService.getInstance().getDatabase();
        DBCollection coll = db.getCollection(SCHEDULE_COLLECTION);
        coll.insert(schedule.getDBObject());
        this.netLogger.debug(netLog.start(CREATE_EVENT+".end"));
        
        //commit to JobRouter
        this.jobRouter.commit(schedule, authzConditions);
        
        this.netLogger.debug(netLog.end(CREATE_EVENT));
    }

    public Schedule getSchedule(String scheduleId, AuthnSubject authnSubject) throws AuthorizationException {
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(GET_EVENT));
        
        //check if can query at all 
        MPSchedulingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.QUERY, null);
        
        DB db = MPSchedulingService.getInstance().getDatabase();
        DBCollection coll = db.getCollection(SCHEDULE_COLLECTION);
        Schedule schedule = null;
        try{
            //ObjectId constructor provides *some* validation
            DBObject dbObj = coll.findOne(new BasicDBObject("_id", new ObjectId(scheduleId)));
            if(dbObj != null){
                schedule = new Schedule(dbObj);
            }
        }catch(Exception e){
            log.debug("ID not found: " + e.getMessage());
        }
        
        //make sure can view selected schedule
        MPSchedulingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.QUERY, schedule);
        
        this.netLogger.debug(netLog.end(GET_EVENT));
        return schedule;
    }

    public boolean deleteSchedule(String scheduleId, AuthnSubject authnSubject) throws AuthorizationException {
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(DELETE_EVENT));
        
        //get the schedule so we can verify user is authorized to delete it
        Schedule schedule = this.getSchedule(scheduleId, authnSubject);
        if(schedule == null){
            this.netLogger.debug(netLog.end(DELETE_EVENT));
            return false;
        }
        
        //check if can query at all 
        MPSchedulingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.DELETE, schedule);
        
        DB db = MPSchedulingService.getInstance().getDatabase();
        DBCollection coll = db.getCollection(SCHEDULE_COLLECTION);
        try{
            //ObjectId constructor provides *some* validation
            coll.remove(new BasicDBObject("_id", new ObjectId(scheduleId)));
        }catch(Exception e){
            log.error("Unable to delete schedule: " + e.getMessage());
            this.netLogger.debug(netLog.error(DELETE_EVENT, e.getMessage()));
            e.printStackTrace();
            throw new RuntimeException("Unable to delete schedule because a database error occurred");
        }
        
        this.netLogger.debug(netLog.end(DELETE_EVENT));
        
        return true;
    }
}
