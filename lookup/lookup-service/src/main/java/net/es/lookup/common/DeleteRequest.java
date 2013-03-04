package net.es.lookup.common;

import java.util.Map;

public abstract class DeleteRequest extends Message {

    public DeleteRequest() {

        super();

    }

    public DeleteRequest(Map<String, Object> map) {

        super(map);

    }

}