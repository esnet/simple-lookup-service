package net.es.lookup.common;

import java.util.Map;

public abstract class QueryRequest extends Message {

    public QueryRequest() {

        super();

    }

    public QueryRequest(Map<String, Object> map) {

        super(map);

    }

}