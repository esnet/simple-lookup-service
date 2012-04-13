package net.es.mp.measurement.types;

import com.mongodb.DBObject;

public class OWAMPResult extends MeasurementResult{
    
    static final public String LOSS = "loss";
    static final public String MIN_DELAY = "minDelay";
    static final public String MEDIAN_DELAY = "medianDelay";
    static final public String MAX_DELAY = "maxDelay";
    static final public String MIN_TTL = "minTTL";
    static final public String MAX_TTL = "maxTTL";
    static final public String MAX_ERROR = "maxError";
    static final public String DUPLICATES = "duplicates";
    
    static final public String STATUS_DENIED = "DENIED";
    static final public String STATUS_CTRL_CONNECT_FAILED = "CTRL_CONNECT_FAILED";
    static final public String STATUS_DATA_CONNECT_FAILED = "DATA_CONNECT_FAILED";
    
    public OWAMPResult(DBObject dbo) {
        super(dbo);
    }
    
    public void setLoss(Integer value){
        this.dbObject.put(LOSS, value);
    }
    
    public Integer getLoss(Integer value){
        return (Integer) this.getField(LOSS);
    }
    
    public void setMinDelay(Double value){
        this.dbObject.put(MIN_DELAY, value);
    }
    
    public Double getMinDelay(Double value){
        return (Double) this.getField(MIN_DELAY);
    }
    
    public void setMedianDelay(Double value){
        this.dbObject.put(MEDIAN_DELAY, value);
    }
    
    public Double getMedianDelay(Double value){
        return (Double) this.getField(MEDIAN_DELAY);
    }
    
    public void setMaxDelay(Double value){
        this.dbObject.put(MAX_DELAY, value);
    }
    
    public Double getMaxDelay(Double value){
        return (Double) this.getField(MAX_DELAY);
    }
    
    public void setMinTTL(Integer value){
        this.dbObject.put(MIN_TTL, value);
    }
    
    public Integer getMinTTL(Integer value){
        return (Integer) this.getField(MIN_TTL);
    }
    
    public void setMaxTTL(Integer value){
        this.dbObject.put(MAX_TTL, value);
    }
    
    public Integer getMaxTTL(Integer value){
        return (Integer) this.getField(MAX_TTL);
    }
    
    public void setMaxError(Double value){
        this.dbObject.put(MAX_ERROR, value);
    }
    
    public Double getMaxError(Double value){
        return (Double) this.getField(MAX_ERROR);
    }
    
    public void setDuplicates(Integer value){
        this.dbObject.put(DUPLICATES, value);
    }
    
    public Integer getDuplicates(Integer value){
        return (Integer) this.getField(DUPLICATES);
    }
    
}
