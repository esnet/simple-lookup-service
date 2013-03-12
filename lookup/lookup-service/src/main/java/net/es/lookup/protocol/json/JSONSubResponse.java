package net.es.lookup.protocol.json;


import net.es.lookup.common.ReservedKeywords;
import net.es.lookup.common.SubResponse;
import net.es.lookup.common.exception.internal.DataFormatException;

import java.util.Map;

public class JSONSubResponse extends SubResponse {

    public JSONSubResponse() {

        super();

    }


    public JSONSubResponse(Map<String, Object> map) {

        super(map);

    }


}
