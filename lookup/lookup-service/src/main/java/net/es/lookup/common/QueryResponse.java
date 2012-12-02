package net.es.lookup.common;

import java.util.Map;

public abstract class QueryResponse extends Message {

    public QueryResponse() {

        super();

    }

    public QueryResponse(Map<String, Object> map) {

        super(map);

    }

}