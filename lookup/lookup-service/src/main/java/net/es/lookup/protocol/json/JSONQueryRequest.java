package net.es.lookup.protocol.json;

import net.es.lookup.common.QueryRequest;
import net.es.lookup.common.Service;
import java.util.Map;

public class JSONQueryRequest extends QueryRequest {
	public JSONQueryRequest() {
        super();
    }

	public JSONQueryRequest(Map<String,Object> map) {
        super(map);
    }
}