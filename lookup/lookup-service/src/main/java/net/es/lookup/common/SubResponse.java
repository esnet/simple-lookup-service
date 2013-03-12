package net.es.lookup.common;

import java.util.Map;

public abstract class SubResponse extends Message {

    public SubResponse() {

        super();
        this.add(ReservedKeywords.RECORD_TYPE, ReservedKeywords.RECORD_VALUE_SUBSCRIBE);

    }

    public SubResponse(Map<String, Object> map) {

        super(map);


    }

}
