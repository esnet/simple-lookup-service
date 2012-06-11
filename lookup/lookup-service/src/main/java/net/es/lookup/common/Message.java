package net.es.lookup.common;

import org.apache.commons.lang.math.LongRange;

import java.rmi.MarshalledObject;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class  Message {

    

    private final Map<String,Object> keyValues;
    protected int status = 0;
    private int error = 0;
    private String errorMessage = "";

    public Message() {
        this.keyValues = new HashMap<String, Object>();
    }

    public Message(Map<String,Object> map) {
        this.keyValues = map;
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
        return (String) this.getMap().get(ReservedKeywords.SERVICE_URI);
    }

    public long getTTL() {
        Long res = (Long) this.getMap().get(ReservedKeywords.TTL);
        if (res == null) {
            return -1;
        }
        return res;
    }

    public List<String> getServiceType() {
        return  (List<String>) this.getMap().get(ReservedKeywords.SERVICE_TYPE);
    }

    public List<String> getAccessPoint() {
        return  (List<String>) this.getMap().get(ReservedKeywords.ACCESS_POINT);
    }

    public List<String> getServiceName() {
        return  (List<String>) this.getMap().get(ReservedKeywords.SERVICE_NAME);
    }

    public List<String> getServiceDomain() {
        return  (List<String>) this.getMap().get(ReservedKeywords.SERVICE_DOMAIN);
    }
    
    public List<String> getClientUUID() {
        return  (List<String>) this.getMap().get(ReservedKeywords.CLIENT_UUID);
    }

    public List<String> getOperator() {
        return (List<String>) this.getMap().get(ReservedKeywords.OPERATOR);
    }

    public synchronized void setError (int error) {
        this.error = error;
    }

    public synchronized int getError () {
        return this.error;
    }

    public synchronized void setErrorMessage (String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public synchronized String getErrorMessage () {
        return this.errorMessage;
    }
}