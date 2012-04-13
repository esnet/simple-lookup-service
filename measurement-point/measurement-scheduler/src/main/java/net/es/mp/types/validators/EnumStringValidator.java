package net.es.mp.types.validators;

import java.util.HashMap;
import java.util.List;

public class EnumStringValidator implements TypeValidator{
    private HashMap<String,Boolean> allowedMap;
    
    public EnumStringValidator(List<String> allowedValues){
        this.allowedMap = new HashMap<String,Boolean>();
        for(String val : allowedValues){
            this.allowedMap.put(val, true);
        }
    }
    
    public void validate(Object obj) throws InvalidMPTypeException {
        if(!this.allowedMap.containsKey((String)obj)){
            throw new InvalidMPTypeException("Value \"" + obj 
                    + "\" is invalid value");
        }
    }
}
