package net.es.lookup.records;

import net.es.lookup.common.ReservedKeys;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.es.lookup.common.exception.RecordException;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

/**
 * Author: sowmya
 * Date: 9/25/12
 * Time: 2:05 PM
 */
public class Record {

    private final Map<String, Object> keyValues;


    public Record(String type) {

        this.keyValues = new HashMap<String, Object>();
        this.keyValues.put(ReservedKeys.RECORD_TYPE, type);


    }

    public Record(Map<String, Object> map) throws RecordException {

        this.keyValues = new HashMap<String, Object>();
        this.setMap(map);
    }


    public final Map<String, Object> getMap() {

        return this.keyValues;

    }

    public final void setMap(Map<String, Object> map) throws RecordException {

        if (map != null) {
            for (String s : map.keySet()) {
                this.keyValues.put(s, map.get(s));
            }
        }

        if (!this.validate()) {
            throw new RecordException("Error creating record. Record is not formatted correctly");
        }

        this.format();


    }

    //This only formats the record elements. No validation is performed
    protected void format() {

        if (this.keyValues.containsKey(ReservedKeys.RECORD_TYPE)) {
            convertListToString(ReservedKeys.RECORD_TYPE);
        }

        if (this.keyValues.containsKey(ReservedKeys.RECORD_TTL)) {
            convertListToString(ReservedKeys.RECORD_TTL);
        }

        if (this.keyValues.containsKey(ReservedKeys.RECORD_EXPIRES)) {
            convertListToString(ReservedKeys.RECORD_EXPIRES);
        }

        if (this.keyValues.containsKey(ReservedKeys.RECORD_URI)) {
            convertListToString(ReservedKeys.RECORD_URI);
        }


    }

    /*This is used only to convert some of the record-key values to String. Internal method only. Not to be converted to public
     * Calling function must check if key exists since this method assumes that the supplied key exists.  */
    private void convertListToString(String key) {

        if (this.keyValues.get(key) instanceof List) {
            List<String> typeList = (List) this.keyValues.get(key);
            String tmp;
            if (typeList.size() > 0) {
                tmp = (String) typeList.get(0);
            } else {
                tmp = "";
            }
            this.keyValues.put(key, tmp);
        }
    }


    public final Object getValue(String key) {

        return this.keyValues.get(key);

    }

    public synchronized void add(String key, Object value) {

        this.keyValues.put(key, value);

    }

    /**
     * Return a single string stored in a record value.
     *
     * Many record values contain only a single data item, even though the JSON representation
     * is a list of strings.  This method makes retrieving these items easier.
     * @param key
     * @return string value, null if an error
     */
    public final String getStringFromListValue(String key) {
        try {
            return ((List<String>) this.getValue(key)).get(0);
        }
        catch (Exception e) {
            return null;
        }
    }

    /**
     * Store a single string value into a record value.
     *
     * Essentially the dual of getStringFromListValue, this function accepts a single
     * String object and stores it in a List<String> as a value in a record element.
     * @param key
     * @param value
     * @throws RecordException
     */
    public final void addStringAsListValue(String key, String value) throws RecordException {
        if (value != null && !value.isEmpty()) {
            List<String> l = new LinkedList<String>();
            l.add(value);
            this.add(key, l);
        }else{
            throw new RecordException(key + " is empty");
        }
    }

    public String getURI() {

        return (String) this.getMap().get(ReservedKeys.RECORD_URI);

    }


    public long getTTL() {

        String ttl = (String) this.getMap().get(ReservedKeys.RECORD_TTL);
        PeriodFormatter fmt = ISOPeriodFormat.standard();
        Duration duration = fmt.parsePeriod(ttl).toStandardDuration();
        return duration.getStandardSeconds();

    }


    public DateTime getExpires() {

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        return fmt.parseDateTime((String) this.getMap().get(ReservedKeys.RECORD_EXPIRES));
    }


    public String getRecordType() {

        return (String) this.getMap().get(ReservedKeys.RECORD_TYPE);

    }

    public String getRecordState() {

        return (String) this.keyValues.get(ReservedKeys.RECORD_STATE);

    }


    public void setTTL(long ttl) {

        Period p = new Period(ttl);
        PeriodFormatter fmt = ISOPeriodFormat.standard();
        String str = fmt.print(p);
        this.add(ReservedKeys.RECORD_TTL, str);

    }


    public void setExpires(DateTime expires) {

        DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
        String str = fmt.print(expires);
        this.add(ReservedKeys.RECORD_EXPIRES, str);

    }

    public void setRecordState(String state) {

        this.add(ReservedKeys.RECORD_STATE, state);

    }

    public Record duplicate() throws RecordException {

        return new Record(this.getMap());
    }


    /**
     * Do some simple validation on records
     * @return true if valid, false if not
     */
    public boolean validate() {

        // First make sure that we have a values array and it's not empty.
        if (keyValues == null || keyValues.isEmpty()) {
            return false;
        }

        // There must be a record type value, although we don't check the actual type.
        if (!keyValues.containsKey(ReservedKeys.RECORD_TYPE)) {
            return false;
        }

        // Iterate over all of the values and check their types/
        for (String key : this.keyValues.keySet()) {
            Object o = this.keyValues.get(key);

            // Each value must be either a string or a list containing strings.
            if (o instanceof String) {
            } else if (o instanceof List<?>) {
                for (Object obj : (List) o) {
                    if (obj instanceof String) {
                    } else {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }

}
