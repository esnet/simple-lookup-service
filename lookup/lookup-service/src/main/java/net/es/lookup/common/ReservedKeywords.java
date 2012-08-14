package net.es.lookup.common;


public class ReservedKeywords{
	//record-level keys
	public static final String RECORD_TYPE = "record-type";
	public static final String RECORD_TTL = "record-ttl";
	public static final String RECORD_URI = "record-uri";
	public static final String RECORD_OPERATOR = "record-operator";
	public static final String RECORD_EXPIRES = "record-expires";
	public static final String RECORD_SKIP = "record-skip";
	public static final String RECORD_MAXRESULTS = "record-maxresults";
	public static final String RECORD_PRIVATEKEY = "record-privatekey";
	public static final String RECORD_FLAGGED = "record-flagged";
	
	//operator keys
	public static final String RECORD_OPERATOR_SUFFIX = "operator";
	//operator values
	public static final String RECORD_OPERATOR_ALL = "all";
	public static final String RECORD_OPERATOR_ANY = "any";
	public static final String RECORD_OPERATOR_DEFAULT = RECORD_OPERATOR_ALL;
	
	//service level keys
	public static final String RECORD_SERVICE_LOCATOR = "record-service-locator";
	public static final String RECORD_SERVICE_NAME = "record-service-name";
	public static final String RECORD_SERVICE_DOMAIN = "record-service-domain";
	public static final String RECORD_SERVICE_TYPE = "record-service-type";	
	
	//record-values
	public static final String RECORD_VALUE_PS = "service";
	public static final String RECORD_VALUE_DEFAULT = RECORD_VALUE_PS;
}