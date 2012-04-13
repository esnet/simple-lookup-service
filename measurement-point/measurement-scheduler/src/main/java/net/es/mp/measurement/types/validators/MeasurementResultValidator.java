package net.es.mp.measurement.types.validators;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;

import net.es.mp.measurement.types.MeasurementResult;
import net.es.mp.types.validators.BaseTypeValidator;
import net.es.mp.types.validators.FieldDefinition;
import net.es.mp.types.validators.InvalidMPTypeException;
import net.es.mp.types.validators.TypeValidator;

public class MeasurementResultValidator extends BaseTypeValidator{
    protected Map<String, FieldDefinition> okFieldDefs;
    protected Map<String, FieldDefinition> errorFieldDefs;
    
    public MeasurementResultValidator(){
        super();
        this.addFieldDef(MeasurementResult.START_TIME, Date.class, false, false);
        this.addFieldDef(MeasurementResult.END_TIME, Date.class, false, false);
        this.addFieldDef(MeasurementResult.STATUS, String.class, false, false);
        this.addFieldDef(MeasurementResult.MESSAGE, String.class, true, true);
        
        this.okFieldDefs = new HashMap<String, FieldDefinition>();
        this.errorFieldDefs = new HashMap<String, FieldDefinition>();
    }
    
    protected void addOkFieldDef(String name, Class<?> type, 
            boolean undefinedAllowed, boolean nullAllowed){
        this.okFieldDefs.put(name, new FieldDefinition(name, type, 
                undefinedAllowed, nullAllowed));
    }
    
    protected void addOkFieldDef(String name, Class<?> type, 
            boolean undefinedAllowed, boolean nullAllowed, TypeValidator validator){
        this.okFieldDefs.put(name, new FieldDefinition(name, type, 
                undefinedAllowed, nullAllowed, validator));
    }
    
    protected void addErrorFieldDef(String name, Class<?> type, 
            boolean undefinedAllowed, boolean nullAllowed){
        this.errorFieldDefs.put(name, new FieldDefinition(name, type, 
                undefinedAllowed, nullAllowed));
    }
    
    protected void addErrorFieldDef(String name, Class<?> type, 
            boolean undefinedAllowed, boolean nullAllowed, TypeValidator validator){
        this.errorFieldDefs.put(name, new FieldDefinition(name, type, 
                undefinedAllowed, nullAllowed, validator));
    }
    
    public void validate(Object objParam) throws InvalidMPTypeException{
        if(objParam instanceof BasicDBObject){
            objParam = new MeasurementResult((BasicDBObject)objParam);
        }
        super.validate(objParam);
        MeasurementResult result = (MeasurementResult) objParam;
        if(MeasurementResult.STATUS_OK.equals(result.getStatus())){
            this.validateFieldDefinitions(objParam, this.okFieldDefs);
        }else{
            this.validateFieldDefinitions(objParam, this.errorFieldDefs);
        }
    }

}
