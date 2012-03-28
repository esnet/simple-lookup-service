package net.es.lookup.protocol.json;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.RegisterRequest;
import net.sf.json.JSONArray;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

public class JSONRegisterRequest extends RegisterRequest {

    static final int VALID = 1;
    static final int INCORRECT_FORMAT =  2;

    private int status = 0;

    public JSONRegisterRequest (String message) throws DuplicateKeyException {
        this.parseJSON(message);
    }

    private void parseJSON (String message) throws DuplicateKeyException {

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
                this.add(o2.toString(), jo.get(o2));
            }
        }
        this.status = JSONRegisterRequest.VALID;
    }

}