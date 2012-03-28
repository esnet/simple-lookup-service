package net.es.lookup.common;

import java.util.Map;

public interface Message {
    public int getStatus();

    public Map getMap();
    public Object getContent();
}