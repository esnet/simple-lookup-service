package net.es.lookup.protocol.json;

import net.es.lookup.common.QueryRequest;
import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Service;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

public class JSONQueryRequest extends QueryRequest {
    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT =  2;

    private int status = 0;

    public JSONQueryRequest (String message) throws DuplicateKeyException {
        this.parseJSON(message);
    }
	
    private void parseJSON (String message) throws DuplicateKeyException {

        JSONTokener tokener = new JSONTokener(message);

        Object obj = tokener.nextValue();

        if ((obj == null) || !(obj instanceof net.sf.json.JSONArray)) {
            // Incorrect format.
            this.status = JSONQueryRequest.INCORRECT_FORMAT;
            return;
        }
        
        

        JSONArray keyValues = (JSONArray) obj;
        for (Object o : keyValues) {
            JSONObject jo = (JSONObject) o;
            for (Object o2 : jo.keySet()) {
                this.add(o2.toString(), jo.get(o2));
            }
        }
        this.status = JSONQueryRequest.VALID;
    }
}