package net.es.lookup.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Message {

    private final Map<String, Object> keyValues;
    protected int status = 0;
    private int error = 0;
    private String errorMessage = "";

    public Message() {

        this.keyValues = new HashMap<String, Object>();

    }

    public Message(Map<String, Object> map) {

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

    public final Object getKey(Object key) {

        return this.keyValues.get(key);

    }

    public synchronized void add(String key, Object value) {

        this.keyValues.put(key, value);

    }

    public String getURI() {

        return (String) this.getMap().get(ReservedKeywords.RECORD_URI);

    }


    public String getTTL() {

        return (String) this.getMap().get(ReservedKeywords.RECORD_TTL);

    }


    public String getExpires() {

        return (String) this.getMap().get(ReservedKeywords.RECORD_EXPIRES);

    }


    public List<String> getServiceType() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_SERVICE_TYPE);

    }

    public List<String> getRecordType() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_TYPE);

    }


    public List<String> getAccessPoint() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_SERVICE_LOCATOR);

    }


    public List<String> getServiceName() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_SERVICE_NAME);

    }


    public List<String> getServiceDomain() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_SERVICE_DOMAIN);

    }


    public List<String> getClientUUID() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_PRIVATEKEY);

    }


    public List<String> getOperator() {

        return (List<String>) this.getMap().get(ReservedKeywords.RECORD_OPERATOR);

    }


    public synchronized void setError(int error) {

        this.error = error;

    }


    public synchronized int getError() {

        return this.error;

    }


    public synchronized void setErrorMessage(String errorMessage) {

        this.errorMessage = errorMessage;

    }


    public synchronized String getErrorMessage() {

        return this.errorMessage;

    }


    //validates the type of value
    public boolean validate() {

        boolean returnVal = true;

        for (String key : this.keyValues.keySet()) {

            Object o = this.keyValues.get(key);

            if (key.equals(ReservedKeywords.RECORD_URI) || key.equals(ReservedKeywords.RECORD_TTL)) {

                if (o instanceof String) {

                    returnVal = returnVal & true;

                } else {

                    returnVal = returnVal & false;
                    return returnVal;

                }

            } else {

                if (o instanceof List<?>) {

                    for (Object obj : (List) o) {

                        if (obj instanceof String) {

                            returnVal = returnVal & true;

                        } else {

                            returnVal = returnVal & false;
                            return returnVal;

                        }

                    }

                    returnVal = returnVal & true;

                } else {

                    returnVal = returnVal & false;
                    return returnVal;

                }

            }

        }

        return returnVal;

    }


}