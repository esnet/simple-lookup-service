package net.es.lookup.protocol.json;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.ParserException;
import net.es.lookup.common.exception.QueryException;
import net.es.lookup.common.exception.RecordException;
import net.es.lookup.queries.Query;
import net.es.lookup.records.Record;
import net.es.lookup.records.RecordFactory;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONBuilder;
import net.sf.json.util.JSONStringer;

import java.util.*;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 5:19 PM
 */
public class JSONParser {

    public static String toString(Record record) throws ParserException {

        JSONStringer stringer = new JSONStringer();

        Map<String, Object> map = record.getMap();
        Set<Map.Entry<String, Object>> entries = map.entrySet();

        try {

            JSONBuilder tmp = stringer.object();

            for (Map.Entry<String, Object> entry : entries) {

                if (entry.getValue() instanceof String) {
                    tmp = tmp.key(entry.getKey()).array();
                    tmp = tmp.value(entry.getValue());
                    tmp.endArray();

                } else {

                    List<String> tmpvalues = (List<String>) entry.getValue();
                    Iterator<String> it = tmpvalues.iterator();
                    tmp = tmp.key(entry.getKey()).array();

                    while (it.hasNext()) {

                        tmp = tmp.value(it.next());

                    }

                    tmp.endArray();

                }

            }

            tmp.endObject();

        } catch (JSONException e) {

            throw new ParserException("Error parsing Record. Cannot convert to JSON");

        }

        return stringer.toString();

    }


    public static String toString(Query query) throws ParserException {

        JSONStringer stringer = new JSONStringer();

        Map<String, Object> map = query.getMap();
        Set<Map.Entry<String, Object>> entries = map.entrySet();

        try {

            JSONBuilder tmp = stringer.object();

            for (Map.Entry<String, Object> entry : entries) {

                //tmp = tmp.key(entry.getKey()).value(entry.getValue());

                if (entry.getValue() instanceof String) {

                    tmp = tmp.key(entry.getKey()).array();
                    tmp = tmp.value(entry.getValue());
                    tmp.endArray();


                } else if(entry.getValue() instanceof List) {

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

            throw new ParserException("Error parsing Query. Cannot convert to JSON");

        }

        return stringer.toString();

    }


    public static Record toRecord(String jsonString) throws ParserException {


        JSONObject jsonObject = JSONObject.fromObject(jsonString);

        String type;
        if(jsonObject.get(ReservedKeys.RECORD_TYPE) instanceof List){
            type  = (String) ((List) jsonObject.get(ReservedKeys.RECORD_TYPE)).get(0);
        }else{
            type = (String) jsonObject.get(ReservedKeys.RECORD_TYPE);
        }

        Record result;
        try {
            result = RecordFactory.getRecord(type);
            result.setMap(jsonObject);
        } catch (RecordException e) {
            throw new ParserException(e.getMessage());
        }
        return result;


    }

    public static List<Record> toRecords(String jsonString) throws ParserException {

        List<Record> result = new ArrayList<Record>();

        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        Iterator it = jsonArray.iterator();

        while (it.hasNext()){
            JSONObject jobj = JSONObject.fromObject(it.next());
            try {
                String type;
                if(jobj.get(ReservedKeys.RECORD_TYPE) instanceof List){
                    type  = (String) ((List) jobj.get(ReservedKeys.RECORD_TYPE)).get(0);
                }else{
                    type = (String) jobj.get(ReservedKeys.RECORD_TYPE);
                }
                Record r = RecordFactory.getRecord(type);
                r.setMap(jobj);
                result.add(r);
            } catch (RecordException e) {
                throw new ParserException("Error parsing String. Cannot convert to Records");
            }
        }

        return result;


    }


    public static List<Query> toQuery(String jsonString) throws ParserException {

        List<Query> result = new ArrayList<Query>();

        JSONArray jsonArray = JSONArray.fromObject(jsonString);

        Iterator it = jsonArray.iterator();

        while (it.hasNext()){
            JSONObject jobj = JSONObject.fromObject(it.next());
            try {
                Query q = new Query(jobj);
                result.add(q);
            } catch (QueryException e) {
                throw new ParserException("Error parsing String. Cannot convert to Query");
            }
        }

        return result;


    }


}
