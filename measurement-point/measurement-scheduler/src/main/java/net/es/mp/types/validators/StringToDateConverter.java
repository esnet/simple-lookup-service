package net.es.mp.types.validators;

import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class StringToDateConverter implements TypeConverter{

    public Object convert(Object src) {
        DateTimeFormatter isoFormatter = ISODateTimeFormat.dateTimeParser();
        return isoFormatter.parseDateTime((String)src).toDateTimeISO().withZone(DateTimeZone.UTC).toDate();
    }
    
}
