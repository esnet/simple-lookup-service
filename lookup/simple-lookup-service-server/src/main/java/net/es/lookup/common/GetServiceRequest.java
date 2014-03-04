package net.es.lookup.common;

import java.util.Map;

public abstract class GetServiceRequest extends Message {

    public GetServiceRequest() {

        super();

    }

    public GetServiceRequest(Map<String, Object> map) {

        super(map);

    }

}