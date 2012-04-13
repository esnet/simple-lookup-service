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

public class ScheduleManager {
    Logger log = Logger.getLogger(ScheduleManager.class);
    Logger netLogger = Logger.getLogger("netLogger");
    JobRouter jobRouter;
    
    final private String SCHEDULE_COLLECTION = "schedules";
    
    final private String CREATE_EVENT = "mp.scheduler.ScheduleManager.createSchedule";
    final private String GET_EVENT = "mp.scheduler.ScheduleManager.getSchedule";
    
    public ScheduleManager(HashMap<String, MPJobScheduler> jobRoutingTable){
        this.jobRouter = new JobRouter(jobRoutingTable);
    }
   
    public void createSchedule(Schedule schedule, String uriPath, AuthnSubject authnSubject) throws MPSchedulerException, AuthorizationException{
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(CREATE_EVENT));
        
        //authorize 
        AuthzConditions authzConditions = MPSchedulingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.CREATE, schedule);
        
        //generate ID and uri
        String baseURI = MPSchedulingService.getInstance().getContainer().getResourceURL();
        ObjectId id = new ObjectId();
        String uri = IDUtil.generateURI(baseURI, uriPath, id.toString());
        schedule.setID(id);
        schedule.setURI(uri);
        
        //call JobRouter
        URI streamURI = this.jobRouter.submit(schedule, authzConditions);
        schedule.setStreamURI(streamURI.toASCIIString());
        
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
            schedule = new Schedule(dbObj);
        }catch(Exception e){
            log.debug("ID not found: " + e.getMessage());
        }
        
        //make sure can view selected schedule
        MPSchedulingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.QUERY, schedule);
        
        this.netLogger.debug(netLog.end(GET_EVENT));
        return schedule;
    }
}
