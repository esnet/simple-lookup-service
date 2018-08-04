package net.es.lookup.protocol.json;

import java.util.ArrayList;
import java.util.List;
import net.es.lookup.common.RegisterRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONTokener;

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

            for (Object key : jsonObj.keySet()) {
                Object value = jsonObj.get(key);
                if (value instanceof String) {
                    List<String> tmpList = new ArrayList<String>(1);
                    tmpList.add((String) value);
                    this.add(key.toString(), tmpList);
                } else if (value instanceof List) {

                    JSONArray jsonArray = new JSONArray();

                    for(Object o: (List)value){
                        Object tmp = o;
                        if(! (o instanceof String)){
                            tmp = "";
                        }
                        jsonArray.add(tmp);
                    }
                    this.add(key.toString(), jsonArray);
                }
            }
        } else {

            this.status = JSONRegisterRequest.INCORRECT_FORMAT;
            return;
        }

    }


}