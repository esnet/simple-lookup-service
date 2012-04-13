package net.es.mp.types.validators;

import java.net.URI;

public class URIValidator implements TypeValidator {

    public void validate(Object obj) throws InvalidMPTypeException {
        try {
            new URI((String) obj);
        } catch (Exception e) {
            throw new InvalidMPTypeException("URI is not valid");
        }
    }
}
