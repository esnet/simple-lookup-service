package net.es.mp.measurement.types.validators;

import net.es.mp.measurement.types.OWAMPResult;
import net.es.mp.types.validators.InvalidMPTypeException;

import com.mongodb.DBObject;

public class OWAMPResultValidator extends MeasurementResultValidator {
    public OWAMPResultValidator(){
        super();
        this.addOkFieldDef(OWAMPResult.LOSS, Integer.class, false, false);
        this.addOkFieldDef(OWAMPResult.MIN_DELAY, Double.class, false, false);
        this.addOkFieldDef(OWAMPResult.MEDIAN_DELAY, Double.class, false, false);
        this.addOkFieldDef(OWAMPResult.MAX_DELAY, Double.class, false, false);
        this.addOkFieldDef(OWAMPResult.MIN_TTL, Integer.class, false, false);
        this.addOkFieldDef(OWAMPResult.MAX_TTL, Integer.class, false, false);
        this.addOkFieldDef(OWAMPResult.MAX_ERROR, Double.class, false, true);
        this.addOkFieldDef(OWAMPResult.DUPLICATES, Integer.class, false, false);
    }
    
    public void validate(Object obj) throws InvalidMPTypeException {
        OWAMPResult result = new OWAMPResult((DBObject)obj);
        super.validate(result);
    }
}
