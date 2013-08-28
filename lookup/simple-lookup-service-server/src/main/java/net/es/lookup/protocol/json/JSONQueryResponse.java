package net.es.lookup.protocol.json;

import net.es.lookup.common.QueryResponse;

import java.util.Map;

public class JSONQueryResponse extends QueryResponse {

    public JSONQueryResponse() {

        super();

    }


    public JSONQueryResponse(Map<String, Object> map) {

        super(map);

    }

}