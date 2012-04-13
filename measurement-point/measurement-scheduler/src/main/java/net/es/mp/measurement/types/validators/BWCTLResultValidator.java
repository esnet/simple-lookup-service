package net.es.mp.measurement.types.validators;

import java.util.ArrayList;

import com.mongodb.DBObject;

import net.es.mp.measurement.types.BWCTLResult;
import net.es.mp.types.validators.InvalidMPTypeException;
import net.es.mp.types.validators.ListValidator;

public class BWCTLResultValidator extends MeasurementResultValidator{
    public BWCTLResultValidator(){
        super();
        this.addOkFieldDef(BWCTLResult.THROUGHPUT, Long.class, false, false);
        this.addOkFieldDef(BWCTLResult.INTERVALS, ArrayList.class, true, false,
                new ListValidator(new BWCTLResultIntervalValidator()));
    }
    
    public void validate(Object obj) throws InvalidMPTypeException {
        BWCTLResult result = new BWCTLResult((DBObject)obj);
        super.validate(result);
    }
}
