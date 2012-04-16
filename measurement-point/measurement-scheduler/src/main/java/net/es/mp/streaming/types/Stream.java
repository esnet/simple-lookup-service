package net.es.mp.streaming.types;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

import net.es.mp.measurement.types.Measurement;
import net.es.mp.types.MPType;

public class Stream extends MPType{
    
    static final public String MEASUREMENTS = "measurements";
    static final public String EXPIRATION = "expiration";
    static final public String SCHEDULE_URI = "schedule-uri";
    
    public Stream(DBObject dbo) {
        super(dbo);
        if(!this.dbObject.containsField(MEASUREMENTS) ||
                this.dbObject.get(MEASUREMENTS) == null){
            this.dbObject.put(MEASUREMENTS, new BasicDBList());
        }
    }
    
    public void setScheduleURI(String value){
        this.dbObject.put(SCHEDULE_URI, value);
    }
    
    public String getScheduleURI(){
        return (String) this.getField(SCHEDULE_URI);
    }
    
    public void setExpiration(String value){
        this.dbObject.put(EXPIRATION, value);
    }
    
    public Date getExpiration(){
        return (Date) this.getField(EXPIRATION);
    }
    
    /**
     * DO NOT use this to add new measurements. See addMeasurementResult.
     * @return
     */
    public List<Measurement> getMeasurements(){
        if(!this.dbObject.containsField(MEASUREMENTS) || this.dbObject.get(MEASUREMENTS) == null){
            return null;
        }
        List<Measurement> measurements = new ArrayList<Measurement>();
        for(DBObject result : (List<DBObject>)this.dbObject.get(MEASUREMENTS)){
            measurements.add(new Measurement(result));
        }
        
        return measurements;
    }
    
    public void addMeasurement(Measurement result){
        if(!this.dbObject.containsField(MEASUREMENTS) || this.dbObject.get(MEASUREMENTS) == null){
            this.dbObject.put(MEASUREMENTS, new ArrayList<DBObject>());
        }
        ((List<DBObject>)this.dbObject.get(MEASUREMENTS)).add(result.getDBObject());
    }

}
