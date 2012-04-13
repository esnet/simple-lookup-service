package net.es.mp.types.validators;

import java.util.List;

public class ListValidator implements TypeValidator {
    TypeValidator itemValidator;
    
    public ListValidator(TypeValidator itemValidator){
        this.itemValidator = itemValidator;
    }
    
    public void validate(Object obj) throws InvalidMPTypeException {
        List<Object> items = (List<Object>) obj;
        for(Object item : items){
            this.itemValidator.validate(item);
        }
    }

}
