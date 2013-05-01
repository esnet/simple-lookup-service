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

    //group keys
    public static final String RECORD_GROUP_DOMAINS = "group-domains" ;

    //location keys
    public static final String RECORD_LOCATION_SITENAME = "location-sitename";
    public static final String RECORD_LOCATION_CITY = "location-city";
    public static final String RECORD_LOCATION_REGION = "location-region";
    public static final String RECORD_LOCATION_COUNTRY = "location-country";
    public static final String RECORD_LOCATION_ZIPCODE = "location-zipcode";
    public static final String RECORD_LOCATION_LATITUDE = "location-latitude";
    public static final String RECORD_LOCATION_LONGITUDE = "location-longitude";

    //service keys
    public static final String RECORD_SERVICE_NAME = "service-name";
    public static final String RECORD_SERVICE_VERSION = "service-version";
    public static final String RECORD_SERVICE_TYPE = "service-type";
    public static final String RECORD_SERVICE_LOCATOR = "service-locator";
    public static final String RECORD_SERVICE_ADMINISTRATORS = "service-administrators" ;
    public static final String RECORD_SERVICE_HOST = "service-host";

    //interface keys
    public static final String RECORD_INTERFACE_ADDRESSES = "interface-addresses";
    public static final String RECORD_INTERFACE_SUBNET = "interface-subnet" ;
    public static final String RECORD_INTERFACE_CAPACITY = "interface-capacity" ;
    public static final String RECORD_INTERFACE_MACADDRESS = "interface-mac";

    public static final String ERROR_MESSAGE = "error-message";
    public static final String ERROR_CODE = "error-code";
}