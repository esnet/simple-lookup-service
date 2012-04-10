package net.es.lookup.protocol.json;

import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;

import java.util.List;
import java.util.Map;
import java.util.Set;


public class JSONMessage {

    public static final String LIST_OBJECT = "results";

    public static String toString (Message message ) {

        JSONStringer stringer = new JSONStringer();

        Map<String, Object> map = message.getMap();
        Set<Map.Entry<String, Object>> entries=  map.entrySet();

        JSONBuilder tmp = stringer.object();

        for (Map.Entry<String,Object> entry : entries) {
            tmp = tmp.key(entry.getKey()).value(entry.getValue());
        }

        tmp.endObject();

        return stringer.toString();
    }

    public static String toString (List<Service> services ) {

        JSONStringer stringer = new JSONStringer();
        JSONBuilder tmp = stringer;

        tmp = tmp.object().key(JSONMessage.LIST_OBJECT).array();

        for (Message service : services) {
            Map<String, Object> map = service.getMap();
            Set<Map.Entry<String, Object>> entries=  map.entrySet();

            tmp = tmp.object();

            for (Map.Entry<String,Object> entry : entries) {
                tmp = tmp.key(entry.getKey()).value(entry.getValue());
            }
            tmp = tmp.endObject();
        }
        tmp = tmp.endArray().endObject();

        return stringer.toString();
    }
}
