package net.es.mp.streaming.types.validators;

import com.mongodb.BasicDBList;

import net.es.mp.streaming.types.Stream;
import net.es.mp.types.validators.MPTypeValidator;

public class StreamValidator extends MPTypeValidator{
    
    public StreamValidator(){
        super();
        this.addFieldDef(Stream.MEASUREMENTS, BasicDBList.class, false, false);
        this.addFieldDef(Stream.SCHEDULE_URI, String.class, false, false);
    }
}
