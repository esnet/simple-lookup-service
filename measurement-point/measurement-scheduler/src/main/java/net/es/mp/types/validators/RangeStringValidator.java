package net.es.mp.types.validators;

public class RangeStringValidator implements TypeValidator {

    public void validate(Object obj) throws InvalidMPTypeException {
        String rangeString = (String) obj;
        if(rangeString == null){
            return;
        }
        String[] rangeParts = rangeString.split(",");
        for(String rangePart : rangeParts){
            String[] rangeBounds = rangePart.split("-");
            if(rangeBounds.length != 2){
                throw new InvalidMPTypeException("Invalid range string provided near " + rangePart);
            }
            for(String rangeBound : rangeBounds){
                if(!rangeBound.trim().matches("\\d+")){
                    throw new InvalidMPTypeException("Invalid range bound found near " + rangeBound);
                }
            }
        }
    }
}
