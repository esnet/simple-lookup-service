package net.es.mp.types.validators;

public interface TypeValidator {
    public void validate(Object obj) throws InvalidMPTypeException;
}
