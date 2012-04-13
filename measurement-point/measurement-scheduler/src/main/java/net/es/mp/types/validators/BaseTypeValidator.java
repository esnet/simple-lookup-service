package net.es.mp.types.validators;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.es.mp.types.BaseType;

abstract public class BaseTypeValidator implements TypeValidator{
    protected Map<String, FieldDefinition> fieldDefs;
    protected Map<String, Map<String,TypeConverter>> converters;
    
    public BaseTypeValidator() {
        this.fieldDefs = new HashMap<String, FieldDefinition>();
        this.converters = new HashMap<String, Map<String,TypeConverter>>();
        
        //add Integer converters
        Map<String,TypeConverter> integerMap = new HashMap<String,TypeConverter>();
        integerMap.put(Long.class.getName(), new IntegerToLongConverter());
        integerMap.put(Double.class.getName(), new IntegerToDoubleConverter());
        this.converters.put(Integer.class.getName(), integerMap);
        
        //add string converters
        Map<String,TypeConverter> stringMap = new HashMap<String,TypeConverter>();
        stringMap.put(Date.class.getName(), new StringToDateConverter());
        this.converters.put(String.class.getName(), stringMap);
    }
    
    protected void addFieldDef(String name, Class<?> type, 
            boolean undefinedAllowed, boolean nullAllowed){
        this.fieldDefs.put(name, new FieldDefinition(name, type, 
                undefinedAllowed, nullAllowed));
    }
    
    protected void addFieldDef(String name, Class<?> type, 
            boolean undefinedAllowed, boolean nullAllowed, TypeValidator validator){
        this.fieldDefs.put(name, new FieldDefinition(name, type, 
                undefinedAllowed, nullAllowed, validator));
    }
    
    public void validate(Object objParam) throws InvalidMPTypeException{
        this.validateFieldDefinitions(objParam, this.fieldDefs);
    }
    
    protected void validateFieldDefinitions(Object objParam, Map<String, FieldDefinition> fields) throws InvalidMPTypeException{
        BaseType obj = (BaseType) objParam;
        Map dboMap = obj.getDBObject().toMap();
        for(String fieldDefName : fields.keySet()){
            System.out.println("fieldDefName=" + fieldDefName);
            FieldDefinition fieldDef = fields.get(fieldDefName);
            Object value = null;
            //check if defined
            if(!dboMap.containsKey(fieldDef.getName()) && !fieldDef.isUndefinedAllowed()){
                throw new InvalidMPTypeException("Field " + fieldDef.getName()  + " must be defined" );
            }else if(!dboMap.containsKey(fieldDef.getName())){
              //nothing left to check - key is undef and its allowed
                System.out.println("Key is undef");
                continue;
            }
            System.out.println("Key defined");
            
            //check if null
            value = dboMap.get(fieldDef.getName());
            if(value == null && !fieldDef.isNullAllowed()){
                throw new InvalidMPTypeException("Field " + fieldDef.getName()  + " cannot be null");
            }else if(value == null){
                //nothing left to check - value is null and its allowed
                System.out.println("Value null");
                continue;
            }
            System.out.println("Value not null");
            
            //check valid type and try conversion
            System.out.println("Start value class: " + value.getClass().getName());
            if(!fieldDef.getType().getName().equals(value.getClass().getName())){
                value = this.convert(value, fieldDef.getType(), fieldDef.getName());
                obj.getDBObject().put(fieldDef.getName(), value);
            }
            System.out.println("End value class: " + value.getClass().getName());
                
            //finally call object's validator
            if(fieldDef.getValidator() != null){
                fieldDef.getValidator().validate(value);
            }
        }
    }
    private Object convert(Object value, Class target, String fieldName) throws InvalidMPTypeException {
        //check for existence of converter from objects type
        if(!this.converters.containsKey(value.getClass().getName()) ||
            this.converters.get(value.getClass().getName()) == null){
            throw new InvalidMPTypeException("Field " + fieldName + 
                    " is an invalid type. No converter found for " + 
                    value.getClass().getName());
        }
        Map<String, TypeConverter> sourceConverters = this.converters.get(value.getClass().getName());
        //now that we have converters from object's type, see if can found one for arget class
        if(!sourceConverters.containsKey(target.getName()) || 
                sourceConverters.get(target.getName()) == null){
            throw new InvalidMPTypeException("Field " + fieldName + 
                    " is an invalid type. No converter found from " + 
                    value.getClass().getName() + " to " + target.getName());
        }
        
        return sourceConverters.get(target.getName()).convert(value);
    }
}
