package net.es.mp.types.validators;

public class IntegerToDoubleConverter implements TypeConverter{

    public Object convert(Object src) {
        return new Double((Integer)src);
    }

}
