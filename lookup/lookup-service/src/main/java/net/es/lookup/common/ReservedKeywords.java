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
    public static final String RECORD_PRIVATEKEY = "record-privatekey";

    //operator keys
    public static final String RECORD_OPERATOR_SUFFIX = "operator";
    //operator values
    public static final String RECORD_OPERATOR_ALL = "all";
    public static final String RECORD_OPERATOR_ANY = "any";
    public static final String RECORD_OPERATOR_DEFAULT = RECORD_OPERATOR_ALL;

    //service level keys
    public static final String RECORD_SERVICE_LOCATOR = "service-locator";
    public static final String RECORD_SERVICE_NAME = "service-name";
    public static final String RECORD_SERVICE_DOMAIN = "service-domain";
    public static final String RECORD_SERVICE_TYPE = "service-type";

    //record-values
    public static final String RECORD_VALUE_PS = "service";
    public static final String RECORD_VALUE_DEFAULT = RECORD_VALUE_PS;
}