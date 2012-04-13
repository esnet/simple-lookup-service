package net.es.mp.types.validators;

public class IntegerToLongConverter implements TypeConverter{

    public Object convert(Object src) {
        return new Long(((Integer)src).longValue());
    }

}
