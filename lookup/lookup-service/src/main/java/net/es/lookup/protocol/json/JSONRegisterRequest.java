package net.es.lookup.protocol.json;

import net.es.lookup.common.KeyValue;
import net.es.lookup.common.Service;
import net.es.lookup.common.RegisterRequest;
import net.es.lookup.common.RegisterResponse;
import net.sf.json.JSONArray;
import net.sf.json.util.JSONStringer;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

public class JSONRegisterRequest implements RegisterRequest {

    static final int VALID = 1;
    static final int INCORRECT_FORMAT =  2;

    private Service service = null;
    private int status = 0;

    public JSONRegisterRequest (String message) {
        this.service = new Service();
        this.parseJSON(message);
    }

    private void parseJSON (String message) {

        JSONTokener tokener = new JSONTokener(message);

        Object obj = tokener.nextValue();

        if ((obj == null) || !(obj instanceof net.sf.json.JSONArray)) {
            // Incorrect format.
            this.status = JSONRegisterRequest.INCORRECT_FORMAT;
            return;
        }

        JSONArray keyValues = (JSONArray) obj;
        for (Object o : keyValues) {
            JSONObject jo = (JSONObject) o;
            for (Object o2 : jo.keySet()) {
                KeyValue keyValue = new KeyValue (o2.toString(), jo.get(o2));
                this.service.addKeyValue(keyValue);
            }
        }
        this.status = JSONRegisterRequest.VALID;
    }

    public int getStatus() {
        return this.status;
    }

    public Service getService () {
        return this.service;
    }

    public Service getContent(){
    	return null;
    }
}