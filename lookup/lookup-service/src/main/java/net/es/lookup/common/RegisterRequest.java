package net.es.lookup.common;

import java.util.Map;

public abstract class RegisterRequest extends Message {

    public RegisterRequest() {

        super();

    }

    public RegisterRequest(Map<String, Object> map) {

        super(map);

    }

}