package net.es.lookup.records;

import net.es.lookup.common.ReservedKeywords;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.es.lookup.common.exception.RecordException;
import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

/**
 * User: sowmya
 * Date: 9/25/12
 * Time: 2:05 PM
 */
public class Record {

    private final Map<String, Object> keyValues;


    public Record(String type) {

        this.keyValues = new HashMap<String, Object>();
        this.keyValues.put(ReservedKeywords.RECORD_TYPE, type);

    }

    public Record(Map<String, Object> map) throws RecordException {

        this.keyValues = map;
        if(!this.validate()){
            throw new RecordException("Error creating record. Missing mandatory key: type");
        }
    }


    public final Map getMap() {

        return this.keyValues;

    }

    public final Object getValue(Object key) {

        return this.keyValues.get(key);

    }

    public synchronized void add(String key, Object value) {

        this.keyValues.put(key, value);

    }

    public String getURI() {

        return (String) this.getMap().get(ReservedKeywords.RECORD_URI);

    }


    public Long getTTL() {

        String ttl = (String) this.getMap().get(ReservedKeywords.RECORD_TTL);
        PeriodFormatter fmt = ISOPeriodFormat.standard();
        Duration duration = fmt.parsePeriod(ttl).toStandardDuration();
        Long ttlval = new Long(duration.getStandardSeconds());
        return ttlval;

    }


    public DateTime getExpires() {

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        DateTime dt = fmt.parseDateTime((String) this.getMap().get(ReservedKeywords.RECORD_EXPIRES));
        return (DateTime) dt;

    }


    public String getRecordType() {

        return (String) this.getMap().get(ReservedKeywords.RECORD_TYPE);

    }

    public String getRecordState() {

        return (String) this.keyValues.get(ReservedKeywords.RECORD_STATE);

    }


    public synchronized void setURI(String uri) {

        this.keyValues.put(ReservedKeywords.RECORD_URI, uri);

    }


    public synchronized void setTTL(Long ttl) {

        Period p = new Period(ttl);
        PeriodFormatter fmt = ISOPeriodFormat.standard();
        String str = fmt.print(p);
        this.keyValues.put(ReservedKeywords.RECORD_TTL, str);

    }


    public synchronized void setExpires(DateTime expires) {

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        String str = fmt.print(expires);
        this.keyValues.put(ReservedKeywords.RECORD_EXPIRES, str);

    }


    public synchronized void setRecordType(String type) {

        this.keyValues.put(ReservedKeywords.RECORD_TYPE, type);

    }

    public synchronized void setRecordState(String state) {

        this.keyValues.put(ReservedKeywords.RECORD_STATE, state);

    }


    //validates the type of value
    public boolean validate() {

        boolean returnVal = true;

        if(!keyValues.containsKey(ReservedKeywords.RECORD_TYPE)){
            returnVal = returnVal & false;
        }

        for (String key : this.keyValues.keySet()) {

            Object o = this.keyValues.get(key);

            if (key.equals(ReservedKeywords.RECORD_URI) || key.equals(ReservedKeywords.RECORD_STATE) || key.equals(ReservedKeywords.RECORD_TYPE) || key.equals(ReservedKeywords.RECORD_TTL) || key.equals(ReservedKeywords.RECORD_EXPIRES)) {

                if (o instanceof String) {

                    returnVal = returnVal & true;

                } else {

                    returnVal = returnVal & false;
                    return returnVal;

                }

            } else {

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

                } else {

                    returnVal = returnVal & false;
                    return returnVal;

                }

            }

        }

        return returnVal;

    }

}
