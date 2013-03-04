package net.es.lookup.records;

import net.es.lookup.common.ReservedKeywords;

import java.util.HashMap;

/**
 * User: sowmya
 * Date: 9/25/12
 * Time: 2:05 PM
 */
public class Record {

    private HashMap<String, Object> record;

    public Record() {

    }


    public Record(String recordType) {

        record.put(ReservedKeywords.RECORD_TYPE, recordType);

    }


    public void setRecordType(String recordType) {

        record.put(ReservedKeywords.RECORD_TYPE, recordType);

    }


    public void setRecordTtl(String recordTtl) {

        record.put(ReservedKeywords.RECORD_TTL, recordTtl);

    }


    public Object getRecordType() {

        return record.get(ReservedKeywords.RECORD_TYPE);

    }


    public Object getRecordTtl() {

        return record.get(ReservedKeywords.RECORD_TTL);

    }


    public Object getRecordUri() {

        return record.get(ReservedKeywords.RECORD_URI);

    }


    public Object getRecordExpires() {

        return record.get(ReservedKeywords.RECORD_EXPIRES);

    }


    public void add(String key, Object value){

        record.put(key,value);

    }

    public Object get(String key){

            return record.get(key);

    }

    public boolean keyExists(String key){

        return record.containsKey(key);

    }

}
