import copy
import json

from voluptuous import Schema
from voluptuous import Extra, Required

from constants import *
from validators import *

class Record(object):
    """Base class for record objects"""
    
    record_schema = Schema({
        Required(LSKey(LS_KEY_TYPE)): LSValues(),
        LSKey(LS_KEY_EXPIRES): LSValues(),
        LSKey(LS_KEY_TTL): LSValues(),
        LSKey(LS_KEY_URI): LSValues(),
        LSKey(LS_KEY_STATE): LSValues(),
        LSKey(LS_KEY_GROUP_COMMUNITIES): LSValues(),
        LSKey(LS_KEY_GROUP_DOMAINS): LSValues(),
        LSKey(LS_KEY_LOCATION_SITENAME): LSValues(),
        LSKey(LS_KEY_LOCATION_CITY): LSValues(),
        LSKey(LS_KEY_LOCATION_REGION): LSValues(),
        LSKey(LS_KEY_LOCATION_STATE): LSValues(),
        LSKey(LS_KEY_LOCATION_COUNTRY): LSValues(),
        LSKey(LS_KEY_LOCATION_CODE): LSValues(),
        LSKey(LS_KEY_LOCATION_LATITUDE): LSValues(),
        LSKey(LS_KEY_LOCATION_LONGITUDE): LSValues(),
        Extra: LSValues()
    })
    
    def __init__(self, data):
        super(Record, self).__init__()
        data = self.record_schema(dict(data))
        super.__setattr__(self, "_data", data)
    
    @property
    def data(self):
        return copy.deepcopy(self._data)
    
    def dump(self, pretty=False):
        if pretty:
            return json.dumps(self._data, sort_keys=True, indent=4)
        else:
            return json.dumps(self._data)
    
    @property
    def pretty(self):
        return self.dump(True)
    
    @property
    def raw(self):
        return self.dump(False)
    
    #TODO: implement delete
    def __getattr__(self, name):
        return self.__getitem__(name.replace("_", "-"))
        
    def __setattr__(self, name, value):
        self.__setitem__(name.replace("_", "-"), value)
    
    def __getitem__(self, name):
        return self._data.__getitem__(name)
    
    def __setitem__(self, name, value):
        self._data.__setitem__(name, value)

class Host(Record):
    """Base class for host record objects"""
    
    def __init__(self, data):
        pass
    
class Interface(Record):
    """Base class for interface record objects"""
    
    def __init__(self, data):
        pass
    
class Person(Record):
    """Base class for person record objects"""
    
    def __init__(self, data):
        pass

class Service(Record):
    """Base class for service record objects"""
    
    def __init__(self, data):
        pass
