package net.es.mp.measurement.types;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.DBObject;

public class BWCTLResult extends MeasurementResult{
    
    static final public String THROUGHPUT = "throughput";
    static final public String INTERVALS = "intervals";
    
    static final public String STATUS_DENIED = "DENIED";
    static final public String STATUS_CTRL_CONNECT_FAILED = "CTRL_CONNECT_FAILED";
    static final public String STATUS_DATA_CONNECT_FAILED = "DATA_CONNECT_FAILED";
    
    public BWCTLResult(DBObject dbo) {
        super(dbo);
    }
    
    public void setThroughput(Long value){
        this.dbObject.put(THROUGHPUT, value);
    }
    
    public Long getThroughput(){
        return (Long) this.getField(THROUGHPUT);
    }
    
    /**
     * DO NOT use this to add new intervals. See addMeasurementResult.
     * @return
     */
    public List<BWCTLResultInterval> getIntervals(){
        if(!this.dbObject.containsField(INTERVALS) || this.dbObject.get(INTERVALS) == null){
            return null;
        }
        List<BWCTLResultInterval> intervals = new ArrayList<BWCTLResultInterval>();
        for(DBObject interval : (List<DBObject>)this.dbObject.get(INTERVALS)){
            intervals.add(new BWCTLResultInterval(interval));
        }
        
        return intervals;
    }
    
    public void addInterval(BWCTLResultInterval interval){
        if(!this.dbObject.containsField(INTERVALS) || this.dbObject.get(INTERVALS) == null){
            this.dbObject.put(INTERVALS, new ArrayList<DBObject>());
        }
        ((List<DBObject>)this.dbObject.get(INTERVALS)).add(interval.getDBObject());
    }
}
