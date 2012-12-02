package net.es.lookup.protocol.json;

import net.es.lookup.common.QueryRequest;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

public class JSONQueryRequest extends QueryRequest {

    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT = 2;


    public JSONQueryRequest(String message) {

        this.parseJSON(message);

    }


    public JSONQueryRequest() {

        super();

    }


    private void parseJSON(String message) {

        JSONTokener tokener = new JSONTokener(message);
        Object obj = tokener.nextValue();
        JSONObject jsonObj = (JSONObject) obj;

        for (Object o : ((JSONObject) obj).keySet()) {

            this.add(o.toString(), ((JSONObject) obj).get(o));

        }

        this.status = JSONRegisterRequest.VALID;

    }


}