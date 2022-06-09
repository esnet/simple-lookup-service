"""import records

# Record types
RECORD_TYPES = {
    "host": records.Host,
    "interface": records.Interface,
    "person": records.Person,
    "service": records.Service
}"""

# Record Keys
LS_KEY_TYPE = "type"
LS_KEY_EXPIRES = "expires"
LS_KEY_TTL = "ttl"
LS_KEY_URI = "uri"
LS_KEY_STATE = "state"

#Type Values
LS_VALUE_TYPE_SERVICE = "service"
LS_VALUE_TYPE_HOST = "host"
LS_VALUE_TYPE_INTERFACE = "interface"
LS_VALUE_TYPE_PERSON = "person"

#State Values
LS_VALUE_STATE = ["registered", "renewed", "deleted", "expired"]

# Operator Keys
LS_KEY_OPERATOR = "operator"
LS_KEY_OPERATOR_SUFFIX = "-operator"

# Operator Values
LS_VALUES_OPERATOR = ["any", "all"]

# Location Keys
LS_KEY_LOCATION_SITENAME = "location-sitename"
LS_KEY_LOCATION_CITY = "location-city"
LS_KEY_LOCATION_REGION = "location-region"
LS_KEY_LOCATION_STATE = "location-state"
LS_KEY_LOCATION_COUNTRY = "location-country"
LS_KEY_LOCATION_CODE = "location-code"
LS_KEY_LOCATION_LATITUDE = "location-latitude"
LS_KEY_LOCATION_LONGITUDE = "location-longitude"

# Group Keys
LS_KEY_GROUP_COMMUNITIES = "group-communities"
LS_KEY_GROUP_DOMAINS = "group-domains"

# Service Keys
LS_KEY_SERVICE_NAME = "service-name"
LS_KEY_SERVICE_TYPE = "service-type"
LS_KEY_SERVICE_VERSION = "service-version"
LS_KEY_SERVICE_HOST = "service-host"
LS_KEY_SERVICE_LOCATOR = "service-locator"
LS_KEY_SERVICE_ADMINISTRATORS = "service-administrators"

# Host Keys
LS_KEY_HOST_NAME = "host-name"
LS_KEY_HOST_HARDWARE_MEMORY = "host-hardware-memory"
LS_KEY_HOST_HARDWARE_PROCESSORSPEED = "host-hardware-processorspeed"
LS_KEY_HOST_HARDWARE_PROCESSORCOUNT = "host-hardware-processorcount"
LS_KEY_HOST_HARDWARE_PROCESSORCORE = "host-hardware-processorcore"

LS_KEY_HOST_OS_NAME = "host-os-name"
LS_KEY_HOST_OS_VERSION = "host-os-version"
LS_KEY_HOST_OS_KERNEL = "host-os-kernel"

LS_KEY_HOST_NET_TCP_CONGESTIONALGORITHM = "host-net-tcp-congestionalgorithm"
LS_KEY_HOST_NET_TCP_MAXBUFFER_SEND = "host-net-tcp-maxbuffer-send"
LS_KEY_HOST_NET_TCP_MAXBUFFER_RECV = "host-net-tcp-maxbuffer-recv"
LS_KEY_HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND = "host-net-tcp-autotunemaxbuffer-send"
LS_KEY_HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV = "host-net-tcp-autotunemaxbuffer-recv"
LS_KEY_HOST_NET_TCP_MAXBACKLOG = "host-net-tcp-maxbacklog"

LS_KEY_HOST_NET_INTERFACES = "host-net-interfaces"
LS_KEY_HOST_ADMINISTRATORS = "host-administrators"

# Interface Keys
LS_KEY_INTERFACE_NAME = "interface-name"
LS_KEY_INTERFACE_ADDRESSES = "interface-addresses"
LS_KEY_INTERFACE_SUBNET = "interface-subnet"
LS_KEY_INTERFACE_CAPACITY = "interface-capacity"
LS_KEY_INTERFACE_MAC = "interface-mac"
LS_KEY_INTERFACE_MTU = "interface-mtu"

# Person Keys
LS_KEY_PERSON_NAME = "person-name"
LS_KEY_PERSON_EMAILS = "person-emails"
LS_KEY_PERSON_PHONENUMBERS = "person-phonenumbers"
LS_KEY_PERSON_ORGANIZATION = "person-organization"
