package net.es.lookup.common;

import java.util.HashMap;
import java.util.Map;

public class  Message {

    public static final String ACCESS_POINT = "access-point";
    public static final String CLIENT_UUID = "client-uuid";
    public static final String AUTHORIZATION = "authorization";
    public static final String TTL = "ttl";
    public static final String SERVICE_NAME = "name";
    public static final String SERVICE_DOMAIN = "domain";
    public static final String SERVICE_TYPE = "service-type";
    public static final String SERVICE_URI = "uri";
    public static final String QUERY_OPERATOR = "operator";

    private final HashMap<String,Object> keyValues = new HashMap<String, Object>();
    private int status = 0;
    private int error = 0;
    private String errorMessage = "";

    public Message() {
    }

    public synchronized int getStatus() {
        return this.status;
    }

    public synchronized int setStatus(int status) {
        int old = this.status;
        this.status = status;
        return old;
    }

    public final Map getMap() {
        return this.keyValues;
    }

    public synchronized void add (String key, Object value) throws DuplicateKeyException {
        if (this.keyValues.containsKey(key)) {
            throw new DuplicateKeyException("Duplicate key: " + key);
        }
        this.keyValues.put(key, value);
    }

    public String getURI() {
        return (String) this.getMap().get(Message.SERVICE_URI);
    }

    public int getTTL() {
        return ((Integer) this.getMap().get(Message.TTL)).intValue();
    }

    public String getOperator() {
        return (String) this.getMap().get(Message.QUERY_OPERATOR);
    }

    public synchronized void setError (int error) {
        this.error = error;
    }

    public synchronized int getError () {
        return this.error;
    }

    public synchronized void setErrorMessage (String errorMessahe) {
        this.errorMessage = errorMessage;
    }

    public synchronized String getErrorMessage () {
        return this.errorMessage;
    }
}