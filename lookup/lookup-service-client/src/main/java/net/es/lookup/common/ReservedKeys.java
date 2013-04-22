package net.es.lookup.common;


public class ReservedKeys {

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

    //subscribe keys
    public static final String RECORD_SUBSCRIBE_QUEUE = "subscribe-queue";
    public static final String RECORD_SUBSCRIBE_LOCATOR = "subscribe-locator";

    //server keys
    public static final String SERVER_STATUS_UNKNOWN = "unknown";
    public static final String SERVER_STATUS_ALIVE = "alive";
    public static final String SERVER_STATUS_UNREACHABLE = "unreachable";

    //operator keys
    public static final String RECORD_OPERATOR_SUFFIX = "operator";

    //service level keys
    public static final String RECORD_SERVICE_LOCATOR = "service-locator";
    public static final String RECORD_SERVICE_NAME = "service-name";
    public static final String RECORD_SERVICE_DOMAIN = "service-domain";
    public static final String RECORD_SERVICE_TYPE = "service-type";


}