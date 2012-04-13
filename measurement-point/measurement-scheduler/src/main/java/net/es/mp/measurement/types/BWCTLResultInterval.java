package net.es.mp.measurement.types;

import com.mongodb.DBObject;

import net.es.mp.types.BaseType;

public class BWCTLResultInterval extends BaseType{
    
    static final public String START = "start";
    static final public String END = "end";
    static final public String THROUGHPUT = "throughput";
    
    public BWCTLResultInterval(DBObject dbo) {
        super(dbo);
    }
    
    public void setStart(Long value){
        this.dbObject.put(START, value);
    }
    
    public Long getStart(){
        return (Long) this.getField(START);
    }
    
    public void setEnd(Long value){
        this.dbObject.put(END, value);
    }
    
    public Long getEnd(){
        return (Long) this.getField(END);
    }
    
    public void setThroughput(Long value){
        this.dbObject.put(THROUGHPUT, value);
    }
    
    public Long getThroughput(){
        return (Long) this.getField(THROUGHPUT);
    }

}
