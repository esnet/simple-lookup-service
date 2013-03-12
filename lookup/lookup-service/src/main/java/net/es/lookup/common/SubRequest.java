package net.es.lookup.common;

import java.util.Map;

public abstract class SubRequest extends Message {

    public SubRequest() {

        super();


    }

    public SubRequest(Map<String, Object> map) {

        super(map);

    }

    public void setDefault(){
        this.add(ReservedKeywords.RECORD_OPERATOR, ReservedKeywords.RECORD_OPERATOR_DEFAULT);
    }

}
