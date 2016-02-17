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

    public final boolean hasKey(Object key) {

        return this.keyValues.containsKey(key);

    }

    public final Object getKey(Object key) {

        return this.keyValues.get(key);

    }

    public synchronized void add(String key, Object value) {

        this.keyValues.put(key, value);

    }

    public String getURI() {

        Object val = this.getMap().get(ReservedKeys.RECORD_URI);

        if(val instanceof String){
            return (String)val;
        }else if(val instanceof List){
            return (String)((List) val).get(0);
        }

        return null;

    }


    public String getTTL() {

        Object val = this.getMap().get(ReservedKeys.RECORD_TTL);
        if(val != null) {
            if (val instanceof String) {
                return (String) val;
            } else if (val instanceof List) {
                if(((List) val).isEmpty()){
                    return "";
                }
                return (String) ((List) val).get(0);
            }
        }

        return null;


    }


    public String getExpires() {

        Object val = this.getMap().get(ReservedKeys.RECORD_EXPIRES);

        if(val instanceof String){
            return (String)val;
        }else if(val instanceof List){
            return (String)((List) val).get(0);
        }

        return null;


    }


    public String getRecordType() {

        Object val = this.getMap().get(ReservedKeys.RECORD_TYPE);

        if(val instanceof String){
            return (String)val;
        }else if(val instanceof List){
            return (String)((List) val).get(0);
        }

        return null;


    }


    public String getOperator() {

        Object val = this.getMap().get(ReservedKeys.RECORD_OPERATOR);

        if(val instanceof String){
            return (String)val;
        }else if(val instanceof List){
            return (String)((List) val).get(0);
        }

        return null;

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

            if (o instanceof List<?>) {
                returnVal = returnVal & true;
                for (Object obj : (List) o) {
                    if (obj instanceof String) {

                        returnVal = returnVal & true;

                    } else{
                        returnVal = returnVal & false;
                        return returnVal;

                    }

                }

            }else if(o instanceof String){
                returnVal = returnVal & true;
            }else {
                returnVal = returnVal & false;
                return returnVal;
            }

        }

        return returnVal;

    }

}