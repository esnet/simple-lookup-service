package net.es.lookup.queries;

import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.QueryException;

import java.util.*;

/**
 * Author: sowmya
 * Date: 4/16/13
 * Time: 4:54 PM
 */
public class Query {

    private final Map<String, Object> keyValues;

    public Query() {

        this.keyValues = new HashMap<String, Object>();
    }

    public Query(Map<String, Object> map) throws QueryException {

        this.keyValues = map;
        if (!this.validate()) {
            throw new QueryException("Error creating query. The map contains invalid value types.");
        }
    }

    public final Map getMap() {

        return this.keyValues;

    }

    public final Object getValue(Object key) {

        return this.keyValues.get(key);

    }

    /*
     value is type List<String> because this implementation of the lookup
     service uses List<String> for all Query values
     */
    public void add(String key, List<String> value) throws QueryException {

        if (key != null && !key.isEmpty() && value != null && !value.isEmpty()) {

            this.keyValues.put(key, value);
        } else {
            throw new QueryException("Invalid key/value pair. Neither can be empty");
        }
    }

    public List<String> getURI() {

        return (List<String>) this.getMap().get(ReservedKeys.RECORD_URI);

    }

    public List<String> getTTL() {

        return (List<String>) this.getMap().get(ReservedKeys.RECORD_TTL);

    }

    public List<String> getExpires() {

        return (List<String>) this.getMap().get(ReservedKeys.RECORD_EXPIRES);

    }

    public List<String> getRecordType() {

        return (List<String>) this.getMap().get(ReservedKeys.RECORD_TYPE);

    }

    public List<String> getRecordState() {

        return (List<String>) this.keyValues.get(ReservedKeys.RECORD_STATE);

    }

    public void setURI(List<String> uri) throws QueryException {

        if (uri != null && !uri.isEmpty()) {

            this.keyValues.put(ReservedKeys.RECORD_URI, uri);
        } else {
            throw new QueryException(ReservedKeys.RECORD_URI + " is empty");
        }
    }

    public void setTTL(List<String> ttl) throws QueryException {

        if (ttl != null && !ttl.isEmpty()) {

            this.keyValues.put(ReservedKeys.RECORD_TTL, ttl);
        } else {
            throw new QueryException(ReservedKeys.RECORD_TTL + " is empty");
        }

    }

    public void setExpires(List<String> expires) throws QueryException {

        if (expires != null && !expires.isEmpty()) {

            this.keyValues.put(ReservedKeys.RECORD_EXPIRES, expires);
        } else {
            throw new QueryException(ReservedKeys.RECORD_EXPIRES + " is empty");
        }

    }

    public void setRecordType(List<String> type) throws QueryException {

        if (type != null && !type.isEmpty()) {

            this.keyValues.put(ReservedKeys.RECORD_TYPE, type);
        } else {
            throw new QueryException(ReservedKeys.RECORD_TYPE + " is empty");
        }
    }

    public void setRecordState(List<String> state) throws QueryException {

        if (state != null && !state.isEmpty()) {

            this.keyValues.put(ReservedKeys.RECORD_STATE, state);
        } else {
            throw new QueryException(ReservedKeys.RECORD_STATE + " is empty");
        }
    }

    public void setKeyOperator(String key, List<String> opValue) throws QueryException {

        if (keyValues.containsKey(key) && opValue != null && opValue.size() == 1 && (opValue.get(0).equalsIgnoreCase("ANY") || opValue.get(0).equalsIgnoreCase("ALL"))) {

            this.keyValues.put((key + "-operator"), opValue);
        } else {
            throw new QueryException("Invalid value for an operator key. Operator keys must have value of either \"ANY\" or \"ALL\".");
        }
    }

    public String toURL() throws QueryException {

        String url = "";

        Set<Map.Entry<String, Object>> entries = keyValues.entrySet();

        boolean isFirstEntry = true;
        for (Map.Entry<String, Object> entry : entries) {

            if (entry.getKey().endsWith(ReservedKeys.RECORD_OPERATOR)) {

                String baseKey = entry.getKey();
                int index = baseKey.indexOf("-" + ReservedKeys.RECORD_OPERATOR);
                baseKey = (index > -1) ? baseKey.substring(0, index) : null;

                if (entry.getKey().endsWith("-" + ReservedKeys.RECORD_OPERATOR) && !keyValues.containsKey(baseKey)) {

                    throw new QueryException("The operator key " + entry.getKey() + " requires the query to have the associated key " + baseKey);
                }

                if (containsInvalidOperatorValues(entry)) {

                    throw new QueryException("Invalid value(s) for the " + entry.getKey() + " key. An operator key must have only one value: either \"ANY\" or \"ALL\".");
                }
            }

            if (entry.getValue() instanceof String) {

                url += (isFirstEntry) ? "?" : "&";
                url += (entry.getKey() + "=" + entry.getValue());
            } else if (entry.getValue() instanceof List) {

                List<String> tmpvalues = (List<String>) entry.getValue();
                Iterator<String> it = tmpvalues.iterator();

                url += (isFirstEntry) ? "?" : "&";
                url += (entry.getKey() + "=");

                boolean isFirstListElement = true;
                while (it.hasNext()) {

                    url += (isFirstListElement) ? "" : ",";
                    url += it.next();
                    isFirstListElement = false;
                }
            }
            isFirstEntry = false;
        }
        return url;
    }

    private boolean containsInvalidOperatorValues(Map.Entry<String, Object> entry) {

        boolean returnVal = false;
        String value = null;

        if (entry.getValue() instanceof List) {

            if (((List) entry.getValue()).size() != 1) {

                return true;
            }
            value = ((List<String>) entry.getValue()).get(0);

        } else if (entry.getValue() instanceof String) {

            value = (String) entry.getValue();
        }

        returnVal &= (value.equalsIgnoreCase("ANY") || value.equalsIgnoreCase("ALL"));

        return returnVal;
    }

    //validates the type of value
    public boolean validate() {

        boolean returnVal = true;

        for (String key : this.keyValues.keySet()) {

            Object o = this.keyValues.get(key);

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


            }

        }

        return returnVal;

    }

}
