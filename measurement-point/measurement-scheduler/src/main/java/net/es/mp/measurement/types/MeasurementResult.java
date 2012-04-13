package net.es.mp.measurement.types;

import java.util.Date;

import com.mongodb.DBObject;

import net.es.mp.types.BaseType;

public class MeasurementResult extends BaseType{
    
    static final public String START_TIME = "startTime";
    static final public String END_TIME = "endTime";
    static final public String STATUS = "status";
    static final public String MESSAGE = "message";
    
    static final public String STATUS_OK = "OK";
    static final public String STATUS_ERROR = "ERROR";
    
    public MeasurementResult(DBObject dbo) {
        super(dbo);
    }
    
    public void setStartTime(Date value){
        this.dbObject.put(START_TIME, value);
    }
    
    public Date getStartTime(){
        return (Date) this.getField(START_TIME);
    }
    
    public void setEndTime(Date value){
        this.dbObject.put(END_TIME, value);
    }
    
    public Date getEndTime(){
        return (Date) this.getField(END_TIME);
    }
    
    public void setStatus(String value){
        this.dbObject.put(STATUS, value);
    }
    
    public String getStatus(){
        return (String) this.getField(STATUS);
    }
    
    public void setMessage(String value){
        this.dbObject.put(MESSAGE, value);
    }
    
    public String getMessage(){
        return (String) this.getField(MESSAGE);
    }
}
