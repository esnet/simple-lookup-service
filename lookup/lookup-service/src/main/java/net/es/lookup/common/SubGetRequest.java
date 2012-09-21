package net.es.lookup.common;

import java.util.Map;

public abstract class SubGetRequest extends Message {

	public SubGetRequest() {

        super();

    }

	public SubGetRequest(Map<String,Object> map) {

        super(map);

    }

}