package net.es.lookup.common;

import java.util.Map;

public abstract class SubResponse extends Message {

    public SubResponse() {

        super();
        this.add(ReservedKeys.RECORD_TYPE, ReservedValues.RECORD_VALUE_TYPE_SUBSCRIBE);

    }

    public SubResponse(Map<String, Object> map) {

        super(map);


    }

}
