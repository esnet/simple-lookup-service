package net.es.mp.measurement.types;

import com.mongodb.DBObject;

import net.es.mp.types.MPType;

public class Measurement extends MPType{
    
    static final public String SCHEDULE_URI = "schedule-uri";
    static final public String RESULT = "result";
    
    public Measurement(DBObject dbo) {
        super(dbo);
    }
    
    public void setScheduleURI(String value){
        this.dbObject.put(SCHEDULE_URI, value);
    }
    
    public String getScheduleURI(){
        return (String) this.getField(SCHEDULE_URI);
    }
    
    public MeasurementResult getResult(){
        return (MeasurementResult) this.getField(RESULT);
    }
    
    public void setResult(MeasurementResult value){
        this.dbObject.put(RESULT, value.getDBObject());
    }
}
