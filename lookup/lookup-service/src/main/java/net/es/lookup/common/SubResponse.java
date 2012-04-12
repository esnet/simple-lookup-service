package net.es.lookup.common;

import java.util.Map;

public abstract class SubResponse extends Message {
	public SubResponse() {
        super();
    }

	public SubResponse(Map<String,Object> map) {
        super(map);
    }
}
