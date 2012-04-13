package net.es.mp.types.validators;

import java.net.InetAddress;

public class EndpointValidator implements TypeValidator{

    public void validate(Object obj) throws InvalidMPTypeException {
        if(obj == null){
            return;
        }
        try {
            InetAddress.getByName((String) obj);
        } catch (Exception e) {
            throw new InvalidMPTypeException("Endpoint must be a hostname or IP address");
        }
    }

}
