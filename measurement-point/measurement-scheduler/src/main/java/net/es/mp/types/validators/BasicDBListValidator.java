package net.es.mp.types.validators;

import com.mongodb.BasicDBList;

public class BasicDBListValidator implements TypeValidator {
    TypeValidator itemValidator;
    
    public BasicDBListValidator(TypeValidator itemValidator){
        this.itemValidator = itemValidator;
    }
    
    public void validate(Object obj) throws InvalidMPTypeException {
        BasicDBList items = (BasicDBList) obj;
        for(Object item : items){
            this.itemValidator.validate(item);
        }
    }

}
