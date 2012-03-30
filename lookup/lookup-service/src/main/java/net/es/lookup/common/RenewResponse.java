package net.es.lookup.common;

import java.util.ArrayList;
import java.util.Map;

import net.es.lookup.common.*;

public abstract class RenewResponse extends Message{
	public RenewResponse() {
        super();
    }

	public RenewResponse(Map<String,Object> map) {
        super(map);
    }
}