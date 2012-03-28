package net.es.lookup.common;

import java.util.Map;

public interface Message {
    public int getStatus();

    public Map getKeyValueMap();
    public Object getContent();
}