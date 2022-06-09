package net.es.lookup.common;

import java.util.Map;

public abstract class GetServiceResponse extends Message {

    public GetServiceResponse() {

        super();

    }

    public GetServiceResponse(Map<String, Object> map) {

        super(map);

    }

}