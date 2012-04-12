package net.es.lookup.common;

import java.util.Map;

public abstract class SubGetResponse extends Message {
	public SubGetResponse() {
        super();
    }

	public SubGetResponse(Map<String,Object> map) {
        super(map);
    }
}