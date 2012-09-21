package net.es.lookup.protocol.json;

import java.util.Map;
import net.es.lookup.common.Service;
import net.es.lookup.common.DeleteResponse;

public class JSONDeleteResponse extends DeleteResponse{

	public JSONDeleteResponse() {

        super();

    }


	public JSONDeleteResponse(Map<String,Object> map) {

        super(map);

    }


}