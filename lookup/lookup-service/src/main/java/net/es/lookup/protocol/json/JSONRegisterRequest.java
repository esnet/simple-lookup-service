package net.es.lookup.protocol.json;

import net.es.lookup.common.RegisterRequest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import java.util.Set;

public class JSONRegisterRequest extends RegisterRequest {

    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT = 2;

    public JSONRegisterRequest(String message) {

        this.parseJSON(message);

    }

    private void parseJSON(String message) {

        JSONTokener tokener = new JSONTokener(message);
        Object obj;

        try {

            obj = tokener.nextValue();

        } catch (JSONException e) {

            this.status = JSONRegisterRequest.INCORRECT_FORMAT;
            return;

        }

        if (obj != null) {

            JSONObject jsonObj = (JSONObject) obj;
            Set keyValues = jsonObj.entrySet();

            for (Object o : ((JSONObject) obj).keySet()) {

                this.add(o.toString(), ((JSONObject) obj).get(o));

            }

        } else {

            this.status = JSONRegisterRequest.INCORRECT_FORMAT;

        }

    }


}