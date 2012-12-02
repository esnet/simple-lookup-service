package net.es.lookup.common;

import java.util.Map;

public abstract class RegisterResponse extends Message {

    public RegisterResponse() {

        super();

    }

    public RegisterResponse(Map<String, Object> map) {

        super(map);

    }

}