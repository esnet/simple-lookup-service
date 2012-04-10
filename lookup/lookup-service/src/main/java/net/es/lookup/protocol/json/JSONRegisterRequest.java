package net.es.lookup.protocol.json;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.convert.ConverterManager;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import net.es.lookup.common.DuplicateKeyException;
import net.es.lookup.common.Message;
import net.es.lookup.common.RegisterRequest;
import net.sf.json.JSONArray;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

public class JSONRegisterRequest extends RegisterRequest {

    static public final int VALID = 1;
    static public final int INCORRECT_FORMAT =  2;

    public JSONRegisterRequest (String message) throws DuplicateKeyException {
        this.parseJSON(message);
    }

    private void parseJSON (String message) throws DuplicateKeyException {

        JSONTokener tokener = new JSONTokener(message);

        Object obj = tokener.nextValue();

        JSONObject jsonObj = (JSONObject) obj;
        Set keyValues = jsonObj.entrySet();
        for (Object o : ((JSONObject) obj).keySet()) {
            // Decode TTL
            if (o.toString().equals(Message.TTL)) {
                PeriodFormatter fmt = ISOPeriodFormat.standard();
                Duration duration = fmt.parsePeriod((String) ((JSONObject) obj).get(o)).toStandardDuration();
                this.add(o.toString(), new Long(duration.getStandardSeconds()));
            } else {
                this.add(o.toString(), ((JSONObject) obj).get(o));
            }
        }

        this.status = JSONRegisterRequest.VALID;
    }

    public String toString() {

        JSONStringer stringer = new JSONStringer();

        Map <String, Object> map = this.getMap();
        Set<Entry <String, Object>> entries=  map.entrySet();
        JSONBuilder builder = stringer.object();
        for (Entry<String,Object> entry : entries) {
            builder.object().key(entry.getKey()).value(entry.getValue()).endObject();
        }
        builder.endObject();

        return stringer.toString();
    }

}