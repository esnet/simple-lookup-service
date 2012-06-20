package net.es.lookup.protocol.json;

import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import net.es.lookup.common.Message;
import net.es.lookup.common.RegisterRequest;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;
import net.sf.json.JSONException;

import net.es.lookup.common.ReservedKeywords;

import net.es.lookup.common.exception.internal.DataFormatException;

public class JSONRegisterRequest extends RegisterRequest {

    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT = 2;

    public JSONRegisterRequest (String message) {
        this.parseJSON(message);
    }

    private void parseJSON (String message) {
    	
    	JSONTokener tokener = new JSONTokener(message);
    	Object obj;
    	try{
    	       obj = tokener.nextValue();
    	}catch(JSONException e){
    		this.status = JSONRegisterRequest.INCORRECT_FORMAT;
    		return;
    	}
     
    	if(obj != null){
    	       JSONObject jsonObj = (JSONObject) obj;
    	        Set keyValues = jsonObj.entrySet();
    	        System.out.println(obj.toString());
    	        for (Object o : ((JSONObject) obj).keySet()) {
    	            // Decode TTL
    	            if (o.toString().equals(ReservedKeywords.RECORD_TTL)) {
    	                PeriodFormatter fmt = ISOPeriodFormat.standard();
    	                Duration duration;
    	                try{
    	                	duration = fmt.parsePeriod((String) ((JSONObject) obj).get(o)).toStandardDuration();
    	                }catch(IllegalArgumentException e){
    	                	this.status=JSONRegisterRequest.INCORRECT_FORMAT;
    	                	return;
    	                }
    	                
    	                this.add(o.toString(), new Long(duration.getStandardSeconds()));
    	            } else {
    	                this.add(o.toString(), ((JSONObject) obj).get(o));
    	            }
    	        }
    	        this.status = JSONRegisterRequest.VALID;
    	}else{
    		this.status = JSONRegisterRequest.INCORRECT_FORMAT;
    	}
 

        

 
    }
}