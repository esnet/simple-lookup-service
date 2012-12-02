package net.es.lookup.protocol.json;

import net.es.lookup.common.RenewRequest;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

import java.util.Set;

public class JSONRenewRequest extends RenewRequest {

    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT = 2;

    public JSONRenewRequest(String message) {

        this.parseJSON(message);

    }

    private void parseJSON(String message) {

        try {

            JSONTokener tokener = new JSONTokener(message);

            Object obj = tokener.nextValue();

            JSONObject jsonObj = (JSONObject) obj;
            Set keyValues = jsonObj.entrySet();

            for (Object o : ((JSONObject) obj).keySet()) {

                this.add(o.toString(), ((JSONObject) obj).get(o));

            }

            this.status = JSONRenewRequest.VALID;

        } catch (JSONException e) {

            this.status = JSONRenewRequest.INCORRECT_FORMAT;

        }

    }


}