package net.es.lookup.common;

import java.util.Map;

public class BulkRegisterRequest extends Message{

    public BulkRegisterRequest(){
        super();
    }

    public BulkRegisterRequest(Map<String, Object> map){
        super(map);
    }
}
