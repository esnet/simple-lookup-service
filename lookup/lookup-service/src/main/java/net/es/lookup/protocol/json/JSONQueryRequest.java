package net.es.lookup.protocol.json;

import net.es.lookup.common.Message;
import net.es.lookup.common.QueryRequest;
import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Service;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONArray;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;
import org.joda.time.Duration;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

public class JSONQueryRequest extends QueryRequest {
    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT =  2;

    public JSONQueryRequest (String message) throws DuplicateKeyException {
        this.parseJSON(message);
    }
	
    private void parseJSON (String message) throws DuplicateKeyException {

        JSONTokener tokener = new JSONTokener(message);

        Object obj = tokener.nextValue();

        JSONObject jsonObj = (JSONObject) obj;
        for (Object o : ((JSONObject) obj).keySet()) {
            this.add(o.toString(), ((JSONObject) obj).get(o));
        }

        this.status = JSONRegisterRequest.VALID;
    }
}