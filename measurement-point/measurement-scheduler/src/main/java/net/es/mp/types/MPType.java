package net.es.mp.types;

import com.mongodb.DBObject;

abstract public class MPType extends BaseType{
    
    static final public String TYPE = "type";
    static final public String URI = "uri";
    
    public MPType(DBObject dbo) {
        super(dbo);
    }
    
    public void setType(String value){
        dbObject.put(TYPE, value);
    }
    
    public String getType(){
        return (String) this.getField(TYPE);
    }
    
    public void setURI(String value){
        dbObject.put(URI, value);
    }
    
    public String getURI(){
        return (String) this.getField(URI);
    }
    
}
