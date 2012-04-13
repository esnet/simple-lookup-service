package net.es.mp.streaming.types.validators;

import java.util.ArrayList;

import net.es.mp.streaming.types.Stream;
import net.es.mp.types.validators.MPTypeValidator;

public class StreamValidator extends MPTypeValidator{
    
    public StreamValidator(){
        super();
        this.addFieldDef(Stream.MEASUREMENTS, ArrayList.class, false, false);
        this.addFieldDef(Stream.SCHEDULE_URI, String.class, false, false);
    }
}
