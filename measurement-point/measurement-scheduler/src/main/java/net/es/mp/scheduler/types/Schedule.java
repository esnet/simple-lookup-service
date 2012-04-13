package net.es.mp.scheduler.types;

import java.util.Date;

import com.mongodb.DBObject;

import net.es.mp.types.MPType;

public class Schedule extends MPType{
    static final public String START_TIME = "startTime";
    static final public String REPEAT = "repeat";
    static final public String INTERVAL = "interval";
    static final public String STREAM_URI = "stream-uri";
    
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
}
