package net.es.lookup.protocol.json;

import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;
import net.sf.json.JSONException;

import java.util.Iterator;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import net.es.lookup.common.exception.internal.DataFormatException;


public class JSONMessage {

    public static final String LIST_OBJECT = "results";

    public static String toString (Message message ) throws DataFormatException {

        JSONStringer stringer = new JSONStringer();

        Map<String, Object> map = message.getMap();
        Set<Map.Entry<String, Object>> entries=  map.entrySet();
        
        try{

        	JSONBuilder tmp = stringer.object();

        	for (Map.Entry<String,Object> entry : entries) {

        		//tmp = tmp.key(entry.getKey()).value(entry.getValue());
        		
        		if(entry.getValue() instanceof String){

            		tmp = tmp.key(entry.getKey()).value(entry.getValue());

            	}else{

            		List<String> tmpvalues = (List)entry.getValue();
            		Iterator<String> it = tmpvalues.iterator();
            		tmp = tmp.key(entry.getKey());
            		tmp = tmp.array();

            		while (it.hasNext()){

            			tmp = tmp.value(it.next());

            		}

            		tmp.endArray();

            	}

        	}

        	tmp.endObject();

        }catch(JSONException e){

        	throw new DataFormatException("Error in data format");

        }

        return stringer.toString();

    }


    public static String toString (List<Service> services ) throws DataFormatException {

        JSONStringer stringer = new JSONStringer();
        JSONBuilder tmp = stringer;

        try{

	        tmp = tmp.object().key(JSONMessage.LIST_OBJECT).array();
	
	        for (Message service : services) {

	            Map<String, Object> map = service.getMap();
	            Set<Map.Entry<String, Object>> entries=  map.entrySet();
	            tmp = tmp.object();
	
	            for (Map.Entry<String,Object> entry : entries) {

	            	if(entry.getValue() instanceof String){

	            		tmp = tmp.key(entry.getKey()).value(entry.getValue());

	            	}else{

	            		List<String> tmpvalues = (List)entry.getValue();
	            		Iterator<String> it = tmpvalues.iterator();
	            		tmp = tmp.key(entry.getKey());
	            		tmp = tmp.array();

	            		while (it.hasNext()){

	            			tmp = tmp.value(it.next());

	            		}

	            		tmp.endArray();

	            	}

	            }

	            tmp = tmp.endObject();

	        }

	        tmp = tmp.endArray().endObject();

        }catch(JSONException e){

        	throw new DataFormatException("Error in data format");

        }

        return stringer.toString();

    }


}
