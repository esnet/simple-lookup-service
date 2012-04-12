package net.es.lookup.protocol.json;


import net.es.lookup.common.SubGetRequest;
import net.es.lookup.common.DuplicateKeyException;

import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

public class JSONSubGetRequest extends SubGetRequest {
    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT =  2;

    public JSONSubGetRequest (String message) throws DuplicateKeyException {
        this.parseJSON(message);
    }

    private void parseJSON (String message) throws DuplicateKeyException {

        JSONTokener tokener = new JSONTokener(message);

        Object obj = tokener.nextValue();

        JSONObject jsonObj = (JSONObject) obj;
        for (Object o : ((JSONObject) obj).keySet()) {
            this.add(o.toString(), ((JSONObject) obj).get(o));
        }

        this.status = JSONSubGetRequest.VALID;
    }
}


