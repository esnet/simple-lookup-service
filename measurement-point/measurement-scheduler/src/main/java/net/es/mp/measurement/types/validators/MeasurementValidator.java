package net.es.mp.measurement.types.validators;

import com.mongodb.BasicDBObject;

import net.es.mp.measurement.types.Measurement;
import net.es.mp.types.validators.MPTypeValidator;

public class MeasurementValidator extends MPTypeValidator{
    public MeasurementValidator(){
        this(null);
    }
    
    public MeasurementValidator(MeasurementResultValidator validator){
        super();
        if(validator == null){
            validator = new MeasurementResultValidator();
        }
        this.addFieldDef(Measurement.SCHEDULE_URI, String.class, true, true);
        this.addFieldDef(Measurement.RESULT, BasicDBObject.class, true, true, validator);
    }
}
