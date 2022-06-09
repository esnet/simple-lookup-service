package net.es.lookup.protocol.json;

import net.es.lookup.common.Message;
import net.es.lookup.common.exception.internal.DataFormatException;
import net.sf.json.JSONException;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class provides  methods that convert
 * the sLS records to the API's JSON message format.
 */

public class JSONMessage {

    public static String toString(Message message) throws DataFormatException {

        JSONStringer stringer = new JSONStringer();

        Map<String, Object> map = message.getMap();
        Set<Map.Entry<String, Object>> entries = map.entrySet();

        try {

            JSONBuilder tmp = stringer.object();

            for (Map.Entry<String, Object> entry : entries) {

                if (entry.getValue() instanceof String) {

                    tmp = tmp.key(entry.getKey()).value(entry.getValue());

                } else {

                    List<String> tmpvalues = (List) entry.getValue();
                    Iterator<String> it = tmpvalues.iterator();
                    tmp = tmp.key(entry.getKey());
                    tmp = tmp.array();

                    while (it.hasNext()) {

                        tmp = tmp.value(it.next());

                    }

                    tmp.endArray();

                }

            }

            tmp.endObject();

        } catch (JSONException e) {

            throw new DataFormatException("Error in elements format");

        }

        return stringer.toString();

    }


    public static String toString(List<Message> services) throws DataFormatException {

        JSONStringer stringer = new JSONStringer();
        JSONBuilder tmp = stringer;

        try {

            tmp = tmp.array();

            for (Message service : services) {

                Map<String, Object> map = service.getMap();
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                tmp = tmp.object();

                for (Map.Entry<String, Object> entry : entries) {

                    if (entry.getValue() instanceof String) {

                        tmp = tmp.key(entry.getKey()).value(entry.getValue());

                    } else if (entry.getValue() instanceof List) {

                        List<String> tmpvalues = (List) entry.getValue();
                        Iterator<String> it = tmpvalues.iterator();
                        tmp = tmp.key(entry.getKey());
                        tmp = tmp.array();

                        while (it.hasNext()) {

                            tmp = tmp.value(it.next());

                        }

                        tmp.endArray();

                    }

                }

                tmp = tmp.endObject();

            }

            tmp = tmp.endArray();

        } catch (JSONException e) {

            throw new DataFormatException("Error in elements format");

        }

        return stringer.toString();

    }



    public static String toString(List<Message> services, String listname) throws DataFormatException {

        JSONStringer stringer = new JSONStringer();
        JSONBuilder tmp = stringer;

        if(listname == null || listname.isEmpty()){
            throw new DataFormatException("List name was null");
        }

        try {

            tmp = tmp.object().key(listname).array();

            for (Message service : services) {

                Map<String, Object> map = service.getMap();
                Set<Map.Entry<String, Object>> entries = map.entrySet();
                tmp = tmp.object();

                for (Map.Entry<String, Object> entry : entries) {

                    if (entry.getValue() instanceof String) {

                        tmp = tmp.key(entry.getKey()).value(entry.getValue());

                    } else if (entry.getValue() instanceof List) {

                        List<String> tmpvalues = (List) entry.getValue();
                        Iterator<String> it = tmpvalues.iterator();
                        tmp = tmp.key(entry.getKey());
                        tmp = tmp.array();

                        while (it.hasNext()) {

                            tmp = tmp.value(it.next());

                        }

                        tmp.endArray();

                    }

                }

                tmp = tmp.endObject();

            }

            tmp = tmp.endArray().endObject();

        } catch (JSONException e) {

            throw new DataFormatException("Error in elements format");

        }

        return stringer.toString();

    }


}
