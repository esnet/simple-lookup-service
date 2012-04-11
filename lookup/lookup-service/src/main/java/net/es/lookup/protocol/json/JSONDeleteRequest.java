package net.es.lookup.protocol.json;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.DeleteRequest;
import net.es.lookup.common.Service;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

public class JSONDeleteRequest extends DeleteRequest{
	static public final int VALID = 1;
    static public final int INCORRECT_FORMAT =  2;
    public JSONDeleteRequest (String message) throws DuplicateKeyException {
        this.parseJSON(message);
    }
	
    private void parseJSON (String message) throws DuplicateKeyException {
    	if(!message.isEmpty()){
        JSONTokener tokener = new JSONTokener(message);

        Object obj = tokener.nextValue();

        JSONObject jsonObj = (JSONObject) obj;
        for (Object o : ((JSONObject) obj).keySet()) {
            this.add(o.toString(), ((JSONObject) obj).get(o));
        }
    	}
        this.status = JSONDeleteRequest.VALID;
    	
    }
}