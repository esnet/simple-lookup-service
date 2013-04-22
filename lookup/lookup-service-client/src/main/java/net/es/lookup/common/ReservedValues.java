package net.es.lookup.common;

/**
 * Author: sowmya
 * Date: 4/22/13
 * Time: 12:45 PM
 */
public class ReservedValues {


    public static final String RECORD_VALUE_TYPE_SUBSCRIBE = "subscribe";
    public static final String RECORD_VALUE_TYPE_PERSON = "person";
    public static final String RECORD_VALUE_TYPE_SERVICE = "service";
    public static final String RECORD_VALUE_TYPE_HOST = "host";
    public static final String RECORD_VALUE_TYPE_INTERFACE = "interface";


    //record-state values
    public static final String RECORD_VALUE_STATE_REGISTER = "registered";
    public static final String RECORD_VALUE_STATE_RENEW = "renewed";
    public static final String RECORD_VALUE_STATE_DELETE = "deleted";
    public static final String RECORD_VALUE_STATE_EXPIRE = "expired";


    //operator values
    public static final String RECORD_OPERATOR_ALL = "all";
    public static final String RECORD_OPERATOR_ANY = "any";
    public static final String RECORD_OPERATOR_DEFAULT = RECORD_OPERATOR_ALL;

}
