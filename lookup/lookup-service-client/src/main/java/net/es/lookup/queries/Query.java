package net.es.lookup.queries;

import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.exception.RecordException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 4:54 PM
 */
public class Query {

    private final Map<String, Object> keyValues;


    public Query() {

        this.keyValues = new HashMap<String, Object>();

    }

    public Query(Map<String, Object> map) throws RecordException {

        this.keyValues = map;
        if (!this.validate()) {
            throw new RecordException("Error creating record. Missing mandatory key: type");
        }
    }


    public final Map getMap() {

        return this.keyValues;

    }

    public final Object getValue(Object key) {

        return this.keyValues.get(key);

    }

    public void add(String key, Object value) {

        this.keyValues.put(key, value);

    }

    public List<String> getURI() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_URI);

    }


    public List<String> getTTL() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_TTL);

    }


    public List<String> getExpires() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_EXPIRES);

    }


    public List<String> getRecordType() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_TYPE);

    }

    public List<String> getRecordState() {

        return (List<String>) this.keyValues.get(ReservedKeywords.RECORD_STATE);

    }


    public void setURI(List<String> uri) {

        this.keyValues.put(ReservedKeywords.RECORD_URI, uri);

    }


    public void setTTL(List<String> ttl) {

        this.keyValues.put(ReservedKeywords.RECORD_TTL, ttl);

    }


    public void setExpires(List<String> expires) {


        this.keyValues.put(ReservedKeywords.RECORD_EXPIRES, expires);

    }


    public void setRecordType(List<String> type) {

        this.keyValues.put(ReservedKeywords.RECORD_TYPE, type);

    }

    public void setRecordState(List<String> state) {

        this.keyValues.put(ReservedKeywords.RECORD_STATE, state);

    }


    //validates the type of value
    public boolean validate() {

        boolean returnVal = true;

        for (String key : this.keyValues.keySet()) {

            Object o = this.keyValues.get(key);

            if (o instanceof List<?>) {

                for (Object obj : (List) o) {

                    if (obj instanceof String) {

                        returnVal = returnVal & true;

                    } else {

                        returnVal = returnVal & false;
                        return returnVal;

                    }

                }

                returnVal = returnVal & true;


            }

        }

        return returnVal;

    }

}
