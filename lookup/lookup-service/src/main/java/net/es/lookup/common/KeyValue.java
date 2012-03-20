package net.es.lookup.common;

public class KeyValue {

    public static final String ACCESS_POINT = "access-point";
    public static final String CLIENT_UUID = "client-uuid";
    public static final String AUTHORIZATION = "authorization";
    public static final String TTL = "ttl";
    public static final String SERVICE_NAME = "name";
    public static final String SERVICE_DOMAIN = "domain";
    public static final String SERVICE_TYPE = "service-type";
    public static final String SERVICE_URI = "uri";

    private String key;
    private Object value;

    public KeyValue (String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
