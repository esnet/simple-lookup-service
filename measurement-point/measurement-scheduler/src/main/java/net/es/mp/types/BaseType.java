package net.es.mp.types;

import java.util.Iterator;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.bson.types.ObjectId;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.mongodb.DBObject;
import com.mongodb.util.JSON;

public class BaseType {
    protected DBObject dbObject;
    
    static final public String DB_ID = "_id";
    
    public BaseType(DBObject dbo){
        this.dbObject = dbo;
    }
    
    protected Object getField(String fieldName){
        if(!this.dbObject.containsField(fieldName)){
            return null;
        }
        return this.dbObject.get(fieldName);
    }
    
    public void setID(ObjectId id){
        dbObject.put(DB_ID, id);
    }
    
    public ObjectId getID(){
        return (ObjectId) this.getField(DB_ID);
    }
    
    public DBObject getDBObject(){
        return this.dbObject;
    }
    
    public String toJSONString(){
        //create json object so don't mess with dbo 
        JSONObject jsonObj = JSONObject.fromObject(JSON.serialize(this.getDBObject()));
        
        //remove dates and any other conflicting types
        parseJSON(jsonObj);
        
        return jsonObj.toString();
    }
    
    private void parseJSON(JSONObject jsonObj){
        DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTime();
        //remove ID
        if(jsonObj.containsKey("_id")){
            jsonObj.remove("_id");
        }
        Iterator it = jsonObj.keys();
        while(it.hasNext()){
            String key = (String) it.next();
            Object value = jsonObj.get(key);
            if(value instanceof JSONObject){
                JSONObject valueJSON = (JSONObject) value;
                if(valueJSON.containsKey("$date")){
                    jsonObj.put(key, isoFormatter
                            .parseDateTime((String)valueJSON.get("$date")).
                            toDateTime().withZone(DateTimeZone.UTC).toString());
                }else{
                    parseJSON((JSONObject)value);
                }
            }else if(value instanceof JSONArray){
                JSONArray valueArray = (JSONArray)value;
                for(int i = 0; i < valueArray.size(); i++){
                    Object valueItem = valueArray.get(i);
                    if(valueItem instanceof JSONObject){
                        JSONObject jsonItem = (JSONObject)valueItem;
                        if(jsonItem.containsKey("$date")){
                            //handle list of dates
                            String dateStr = isoFormatter.parseDateTime((String)jsonItem.get("$date")).
                                toDateTime().withZone(DateTimeZone.UTC).toString();
                            valueArray.remove(i);
                            valueArray.add(i, dateStr);
                        }else{
                            //all other objects
                            parseJSON((JSONObject)valueItem);
                        }
                    }
                }
            }
        }
    }
    
    /*
    public String toJSONString(){
        //create json object so don't mess with dbo 
        JSONObject jsonObj = JSONObject.fromObject(JSON.serialize(this.getDBObject()));
        //remove ID
        jsonObj.remove("_id");
        //make sure date is formatted correctly
        for(String dateField : dateFields){
            if(!jsonObj.containsKey(dateField) || 
                    jsonObj.get(dateField) == null){
                continue;
            }
            DateTime dateTime = new DateTime((Date)this.getDBObject().get(dateField));
            jsonObj.put(dateField, dateTime.toDateTimeISO().withZone(DateTimeZone.UTC).toString());
        }
        return jsonObj.toString();
    }
    */
}
