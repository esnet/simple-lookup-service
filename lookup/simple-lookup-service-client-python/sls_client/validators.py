from datetime import datetime, timedelta
import re
import warnings

from isodate import parse_datetime, parse_duration
from isodate import datetime_isoformat, duration_isoformat

from voluptuous import All, Any, Length, Range
from voluptuous import Invalid, MultipleInvalid

from constants import *

LS_MAC_ADDRESS_FORMAT = re.compile(r"^([0-9A-F]{2}[-]){5}([0-9A-F]{2})$|^([0-9A-F]{2}[:]){5}([0-9A-F]{2})$", re.IGNORECASE)
LS_COUNTRY_FORMAT = re.compile(r"^[A-Z]{2}$", re.IGNORECASE)

class LSRecordInfoWarning(Warning): pass
class LSTimestampWarning(LSRecordInfoWarning): pass
class LSDurationWarning(LSRecordInfoWarning): pass
class LSStateWarning(LSRecordInfoWarning): pass
class LSCountryWarning(LSRecordInfoWarning): pass
class LSMACAddressWarning(LSRecordInfoWarning): pass
class LSLatitudeWarning(LSRecordInfoWarning): pass
class LSLongitudeWarning(LSRecordInfoWarning): pass

def LSBase(msg=None):
    def f(v):
        if isinstance(v, basestring):
            v = unicode(v)
        else:
            raise Invalid(msg or "expected string")
        return v
    return f

def LSKey(validator=None, msg=None):
    def f(v):
        if not v.isalnum():
            raise Invalid(msg or "key must be alphanumeric")
        return v
    base = LSBase(msg="key must be a string")
    if validator:
        return All(base, f, validator)
    return All(base, f)

def LSValue(validator=None, msg=None):
    def f(v):
        if isinstance(v, list):
            if len(v) == 1:
                return v[0]
            elif len(v) == 0:
                return unicode("")
            else:
                raise Invalid(msg or "expected single value")
        return v
    base = LSBase(msg="value must be a string")
    if validator:
        return All(f, validator, base)
    return All(f, base)

def LSValues(validator=None, msg=None):
    def f(v):
        if not isinstance(v, list):
            return [v]
        return v
    return All(f, All([LSValue(validator)], Length(min=1)))

def LSTimestamp():
    def f(v):
        if isinstance(v, datetime):
            v = datetime_isoformat(v)
        else:
            try:
                v = datetime_isoformat(parse_datetime(v))
            except:
                warnings.warn("incorrect timestamp format", LSTimestampWarning)
        return v
    return f

def LSDuration():
    def f(v):
        if isinstance(v, timedelta):
            v = duration_isoformat(v)
        else:
            try:
                v = duration_isoformat(parse_duration(v))
            except:
                warnings.warn("incorrect duration format", LSDurationWarning)
        if isinstance(v, basestring) and v.startswith("-"):
            warnings.warn("duration should not be negative", LSDurationWarning)
        return v
    return f

def LSState():
    pass

def LSCountry():
    def f(v):
        if not isinstance(v, basestring) or not LS_COUNTRY_FORMAT.match(v):
            warnings.warn("incorrect country format", LSCountryWarning)
    return f

def LSLatitude():
    def f(v):
        longitude = None
        try:
            latitude = float(v)
        except:
            pass
        if latitude is None or math.abs(latitude) > 90:
            warnings.warn("incorrect latitude format", LSLatitudeWarning)
    return f

def LSLongitude():
    def f(v):
        longitude = None
        try:
            longitude = float(v)
        except:
            pass
        if longitude is None or math.abs(longitude) > 180:
            warnings.warn("incorrect longitude format", LSLongitudeWarning)
    return f

def LSMACAddress():
    def f(v):
        if not isinstance(v, basestring) or not LS_MAC_ADDRESS_FORMAT.match(v):
            warnings.warn("incorrect MAC address format", LSMACAddressWarning)
    return f

def LSNetworkAddress():
    pass
