package net.es.mp.measurement.types.validators;

import com.mongodb.DBObject;

import net.es.mp.measurement.types.BWCTLResultInterval;
import net.es.mp.types.validators.BaseTypeValidator;
import net.es.mp.types.validators.InvalidMPTypeException;

public class BWCTLResultIntervalValidator extends BaseTypeValidator {
    public BWCTLResultIntervalValidator(){
        this.addFieldDef(BWCTLResultInterval.START, Long.class, false, false);
        this.addFieldDef(BWCTLResultInterval.END, Long.class, false, false);
        this.addFieldDef(BWCTLResultInterval.THROUGHPUT, Long.class, false, false);
    }
    
    public void validate(Object obj) throws InvalidMPTypeException {
        BWCTLResultInterval result = new BWCTLResultInterval((DBObject)obj);
        super.validate(result);
    }
}
