package net.es.lookup.common;

/** Author: sowmya
 * Date: 4/22/13
 * Time: 12:45 PM. */
public class ReservedValues {

  public static final String RECORD_VALUE_TYPE_SUBSCRIBE = "subscribe";
  public static final String RECORD_VALUE_TYPE_PERSON = "person";
  public static final String RECORD_VALUE_TYPE_SERVICE = "service";
  public static final String RECORD_VALUE_TYPE_HOST = "host";
  public static final String RECORD_VALUE_TYPE_INTERFACE = "interface";
  public static final String RECORD_VALUE_TYPE_BOOTSTRAP = "bootstrap";
  public static final String RECORD_VALUE_TYPE_PSMETADATA = "psmetadata";

  // record-state values
  public static final String RECORD_VALUE_STATE_REGISTER = "registered";
  public static final String RECORD_VALUE_STATE_RENEW = "renewed";
  public static final String RECORD_VALUE_STATE_DELETE = "deleted";
  public static final String RECORD_VALUE_STATE_EXPIRE = "expired";

  // operator values
  public static final String RECORD_OPERATOR_ALL = "all";
  public static final String RECORD_OPERATOR_ANY = "any";
  public static final String RECORD_OPERATOR_DEFAULT = RECORD_OPERATOR_ALL;

  public static final String RECORD_VALUE_TYPE_ERROR = "error";
  // server keys
  public static final String SERVER_STATUS_UNKNOWN = "unknown";
  public static final String SERVER_STATUS_ALIVE = "alive";
  public static final String SERVER_STATUS_UNREACHABLE = "unreachable";

  public static final String CACHE_TYPE_REPLICATION = "replication";
  public static final String CACHE_TYPE_ARCHIVE = "archive";

  public static final String RECORD_VALUE_TYPE_KEY_VALUE_PAIR = "key-value-pair";

  public static final String SERVICE_MODE_READONLY = "r";
  public static final String SERVICE_MODE_READWRITE = "rw";
  public static final Object RECORD_SUBSCRIBER_QUEUE_STATE_NEW = "new";
  public static final Object RECORD_SUBSCRIBER_QUEUE_STATE_EXISTING = "existing";

  public static final String RECORD_VALUE_UNDERSCORE = "_";
  public static final String RECORD_VALUE_FORWARD_SLASH = "/";

  public static final Object RECORD_SUBSCRIBER_ENDPOINT_ELASTIC = "elastic";

  // elastic search values
  public static String ELASTIC_ALIASES_ENDPOINT = "_aliases";

  public static String ELASTIC_WRITE_ALIAS_SUFFIX = "_write";

  // error messages
  public static final String RECORD_BULKRENEW_EXPIRED_ERRORMESSAGE = "expired";
  public static final String RECORD_BULKRENEW_NOTFOUND_ERRORMESSAGE = "notfound";
}
