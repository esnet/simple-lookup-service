package net.es.lookup.protocol.json;

import net.es.lookup.common.Message;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;

import java.util.Map;
import java.util.Set;


public class JSONMessage {

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
}
