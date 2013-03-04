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
    //public static final String RECORD_FLAGGED = "record-flagged";

    //operator keys
    public static final String RECORD_OPERATOR_SUFFIX = "operator";
    //operator values
    public static final String RECORD_OPERATOR_ALL = "all";
    public static final String RECORD_OPERATOR_ANY = "any";
    public static final String RECORD_OPERATOR_DEFAULT = RECORD_OPERATOR_ALL;

    //service level keys
    public static final String RECORD_SERVICE_LOCATOR = "service-locator";
    public static final String RECORD_SERVICE_NAME = "service-name";
    public static final String RECORD_SERVICE_DOMAINS = "service-domains";
    public static final String RECORD_SERVICE_TYPE = "service-type";
    public static final String RECORD_SERVICE_VERSION = "service-version";
    public static final String RECORD_SERVICE_EVENTTYPE = "service-eventtype";
    public static final String RECORD_SERVICE_CONTACTNAME = "service-contactname";
    public static final String RECORD_SERVICE_CONTACTEMAIL = "service-contactemail";
    public static final String RECORD_SERVICE_EVENTTYPE = "service-eventtype";
    public static final String RECORD_SERVICE_COMMUNITIES = "service-communities";
    public static final String RECORD_SERVICE_LATITUDE = "service-latitude";
    public static final String RECORD_SERVICE_LONGITUDE = "service-longitude";
    public static final String RECORD_SERVICE_HOST = "service-host";


    public static final String RECORD_SERVICE_BWCTLMA_TESTS = "service-bwctlma-tests";
    public static final String RECORD_SERVICE_OWAMPMA_TESTS = "service-owampma-tests";
    public static final String RECORD_SERVICE_OWAMPBUCKETSMA_TESTS = "service-owampbucketsma-tests";
    public static final String RECORD_SERVICE_SNMPMA_TESTS = "service-snmpma-tests";


    //record-values
    public static final String RECORD_VALUE_PS = "service";
    public static final String RECORD_VALUE_DEFAULT = RECORD_VALUE_PS;

}