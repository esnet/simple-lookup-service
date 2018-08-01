package net.es.lookup.protocol.json;

import net.es.lookup.common.GetServiceResponse;
import net.es.lookup.common.Message;

import java.util.Map;

public class JSONGetServiceResponse extends GetServiceResponse {
    public JSONGetServiceResponse() {
        super();
    }


    public JSONGetServiceResponse(Map<String, Object> map) {
        super(map);
    }


}