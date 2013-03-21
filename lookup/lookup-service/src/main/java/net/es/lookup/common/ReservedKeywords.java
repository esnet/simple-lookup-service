package net.es.lookup.common;


public class ReservedKeywords {

    //record-level keys
    public static final String RECORD_TYPE = "type";
    public static final String RECORD_TTL = "ttl";
    public static final String RECORD_URI = "uri";
    public static final String RECORD_OPERATOR = "operator";
    public static final String RECORD_EXPIRES = "expires";
    public static final String RECORD_SKIP = "skip";
    public static final String RECORD_MAXRESULTS = "maxresults";
    public static final String RECORD_PRIVATEKEY = "privatekey";
    public static final String RECORD_STATE = "state";



    //record-values
    public static final String RECORD_VALUE_PS = "service";
    public static final String RECORD_VALUE_SUBSCRIBE = "subscribe";
    public static final String RECORD_VALUE_DEFAULT = RECORD_VALUE_PS;
    public static final String RECORD_VALUE_STATE_REGISTER = "registered";
    public static final String RECORD_VALUE_STATE_RENEW = "renewed";
    public static final String RECORD_VALUE_STATE_DELETE = "deleted";
    public static final String RECORD_VALUE_STATE_EXPIRE = "expired";

    //operator keys
    public static final String RECORD_OPERATOR_SUFFIX = "operator";
    //operator values
    public static final String RECORD_OPERATOR_ALL = "all";
    public static final String RECORD_OPERATOR_ANY = "any";
    public static final String RECORD_OPERATOR_DEFAULT = RECORD_OPERATOR_ALL;


    //subscribe keys
    public static final String RECORD_SUBSCRIBE_QUEUE = "subscribe-queue";
    public static final String RECORD_SUBSCRIBE_LOCATOR = "subscribe-locator";

    //service level keys
    public static final String RECORD_SERVICE_LOCATOR = "service-locator";
    public static final String RECORD_SERVICE_NAME = "service-name";
    public static final String RECORD_SERVICE_DOMAIN = "service-domain";
    public static final String RECORD_SERVICE_TYPE = "service-type";

    public static final String SERVER_STATUS_UNKNOWN = "unknown";
    public static final String SERVER_STATUS_ALIVE = "alive";
    public static final String SERVER_STATUS_UNREACHABLE = "unreachable";
}