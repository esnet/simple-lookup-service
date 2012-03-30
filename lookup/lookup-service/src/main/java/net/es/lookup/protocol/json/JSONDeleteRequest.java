package net.es.lookup.protocol.json;

import net.es.lookup.common.DeleteRequest;
import net.es.lookup.common.Service;
import java.util.Map;

public class JSONDeleteRequest extends DeleteRequest{
	public JSONDeleteRequest() {
        super();
    }

	public JSONDeleteRequest(Map<String,Object> map) {
        super(map);
    }
}