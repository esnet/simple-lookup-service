package net.es.mp.streaming;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authz.AuthorizationException;
import net.es.mp.authz.AuthzAction;
import net.es.mp.measurement.types.Measurement;
import net.es.mp.scheduler.NetLogger;
import net.es.mp.streaming.types.Stream;
import net.es.mp.streaming.types.validators.StreamValidator;
import net.es.mp.types.validators.InvalidMPTypeException;
import net.es.mp.util.IDUtil;

public class StreamManager {
    Logger log = Logger.getLogger(StreamManager.class);
    Logger netLogger = Logger.getLogger("netLogger");
    StreamValidator streamValidator;
    
    final private String STREAM_COLLECTION = "streams";
    
    final private String CREATE_EVENT = "mp.streaming.StreamManager.createStream";
    final private String ADD_MEAS_EVENT = "mp.streaming.StreamManager.addMeasurement";
    final private String GET_EVENT = "mp.streaming.StreamManager.getStream";
    final private String DELETE_EVENT = "mp.streaming.StreamManager.deleteStream";
    
    public StreamManager(){
        this.streamValidator = new StreamValidator();
    }
    
    public void createStream(Stream stream, String uriPath) throws MPStreamingException{
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(CREATE_EVENT));
        
        //generate ID and uri
        String baseURI = MPStreamingService.getInstance().getContainer().getResourceURL();
        ObjectId id = new ObjectId();
        String uri = IDUtil.generateURI(baseURI, uriPath, id.toString());
        stream.setID(id);
        stream.setURI(uri);
        
        //validate 
        try {
            this.streamValidator.validate(stream);
        } catch (InvalidMPTypeException e) {
            this.netLogger.debug(netLog.error(CREATE_EVENT, e.getMessage()));
            e.printStackTrace();
            throw new MPStreamingException("Invalid stream request: " + e.getMessage());
        }
        
        //store
        DB db = MPStreamingService.getInstance().getDatabase();
        DBCollection coll = db.getCollection(STREAM_COLLECTION);
        coll.insert(stream.getDBObject());
        
        this.netLogger.debug(netLog.end(CREATE_EVENT));
    }
    
    public URI addMeasurements(String streamId, List<Measurement> measurements){
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(ADD_MEAS_EVENT));
        ObjectId id = new ObjectId(streamId);
        List<DBObject> dbMeasurements = new ArrayList<DBObject>();
        for(Measurement meas : measurements){
            dbMeasurements.add(meas.getDBObject());
        }
        DB db = MPStreamingService.getInstance().getDatabase();
        DBCollection coll = db.getCollection(STREAM_COLLECTION);
        BasicDBObject query = new BasicDBObject();
        query.put("_id", id);
        BasicDBObject update = new BasicDBObject();
        update.put("$pushAll", new BasicDBObject(Stream.MEASUREMENTS, dbMeasurements));
        coll.update(query, update);
        this.netLogger.debug(netLog.end(ADD_MEAS_EVENT));
        return null;
    }
    
    public Stream getStream(String streamId, AuthnSubject authnSubject) throws AuthorizationException {
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(GET_EVENT));
        
        //authorize
        MPStreamingService.getInstance().getAuthorizer().authorize(authnSubject, 
                AuthzAction.QUERY, null);
        
        DB db = MPStreamingService.getInstance().getDatabase();
        DBCollection coll = db.getCollection(STREAM_COLLECTION);
        Stream stream = null;
        try{
            //ObjectId constructor provides *some* validation
            DBObject dbObj = coll.findOne(new BasicDBObject("_id", new ObjectId(streamId)));
            if(dbObj != null){
                stream = new Stream(dbObj);
            }
        }catch(Exception e){
            log.debug("ID not found: " + e.getMessage());
        }
        
        MPStreamingService.getInstance().getAuthorizer().authorize(authnSubject, 
                AuthzAction.QUERY, stream);
        
        this.netLogger.debug(netLog.end(GET_EVENT));
        return stream;
    }

    public boolean deleteStream(String streamId, AuthnSubject authnSubject) throws AuthorizationException {
        NetLogger netLog = NetLogger.getTlogger();
        this.netLogger.debug(netLog.start(DELETE_EVENT));
        
        //get the stream so we can verify user is authorized to delete it
        Stream stream = this.getStream(streamId, authnSubject);
        if(stream == null){
            this.netLogger.debug(netLog.end(DELETE_EVENT));
            return false;
        }
        
        //check if can query at all 
        MPStreamingService.getInstance().getAuthorizer().authorize(authnSubject, AuthzAction.DELETE, stream);
        
        DB db = MPStreamingService.getInstance().getDatabase();
        DBCollection coll = db.getCollection(STREAM_COLLECTION);
        try{
            //ObjectId constructor provides *some* validation
            coll.remove(new BasicDBObject("_id", new ObjectId(streamId)));
        }catch(Exception e){
            log.error("Unable to delete stream: " + e.getMessage());
            this.netLogger.debug(netLog.error(DELETE_EVENT, e.getMessage()));
            e.printStackTrace();
            throw new RuntimeException("Unable to delete stream because a database error occurred");
        }
        
        this.netLogger.debug(netLog.end(DELETE_EVENT));
        
        return true;
    }
}
