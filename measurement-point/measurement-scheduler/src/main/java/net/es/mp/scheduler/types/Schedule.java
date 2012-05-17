package net.es.mp.scheduler.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import net.es.mp.measurement.types.Measurement;
import net.es.mp.types.MPType;

public class Schedule extends MPType{
    static final public String START_TIME = "startTime";
    static final public String REPEAT = "repeat";
    static final public String INTERVAL = "interval";
    static final public String STREAM_URI = "stream-uri";
    static final public String CALLBACK_URIS = "callback-uris";
    
    public Schedule(DBObject dbo){
        super(dbo);
    }
    
    public void setStartTime(Date value){
        this.dbObject.put(START_TIME, value);
    }
    
    public Date getStartTime(){
        return (Date) this.getField(START_TIME);
    }
    
    public void setRepeat(Integer value){
        this.dbObject.put(REPEAT, value);
    }
    
    public Integer getRepeat(){
        return (Integer)this.getField(REPEAT);
    }
    
    public void setInterval(Long value){
        this.dbObject.put(INTERVAL, value);
    }
    
    public Long getInterval(){
        return (Long) this.getField(INTERVAL);
    }
    
    public void setStreamURI(String streamURI) {
        this.dbObject.put(STREAM_URI, streamURI);
    }

    public String getStreamURI() {
        return (String) this.getField(STREAM_URI);
    }
    
    public void setCallbackURIs(List<String> callbackUris) {
        this.dbObject.put(CALLBACK_URIS, callbackUris);
    }

    /**
     * DO NOT use this to add new URIs. See addCallbackURI.
     * @return
     */
    public List<String> getCallbackURIs(){
        if(!this.dbObject.containsField(CALLBACK_URIS) || this.dbObject.get(CALLBACK_URIS) == null){
            return null;
        }
        List<String> uris = new ArrayList<String>();
        for(Object result : (BasicDBList)this.dbObject.get(CALLBACK_URIS)){
            uris.add((String)result);
        }
        
        return uris;
    }
    
    public void addCallbackURI(String uri){
        if(!this.dbObject.containsField(CALLBACK_URIS) || this.dbObject.get(CALLBACK_URIS) == null){
            this.dbObject.put(CALLBACK_URIS, new BasicDBList());
        }
        ((BasicDBList)this.dbObject.get(CALLBACK_URIS)).add(uri);
    }
}
