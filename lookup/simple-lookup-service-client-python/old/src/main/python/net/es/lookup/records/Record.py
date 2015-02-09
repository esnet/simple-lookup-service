from net.es.lookup.common.exception.RecordException import RecordException

__author__ = 'student5'


class Record:

    __keyValues = {}

    def __init__(self, type):
        self.__keyValues = dict('type', type)

    def __init__(self, recordMap):
        self.__keyValues = dict(recordMap)

    def getMap(self):
        return self.__keyValues

    def setMap(self, recordMap):
        if recordMap is not None:
            self.__keyValues = recordMap
        else:
            raise RecordException("Record map is undefined")

        if not self.validate():
            raise RecordException("Error creating record. Missing mandatory key: type")

        self.format()

    #TODO: un-hardcode these
    def format(self):
        if 'type' in self.__keyValues and isinstance(self.__keyValues.get('type'), list):
            self.__keyValues['type'] = self.__keyValues.get('type')[0]

        if dict(self.__keyValues).has_key('ttl') and isinstance(self.__keyValues.get('ttl'), list):
            ttl = ''
            if self.__keyValues.get('ttl'):
                ttl = self.__keyValues.get('ttl')[0]
            self.__keyValues['ttl'] = ttl

        if dict(self.__keyValues).has_key('expires') and isinstance(self.__keyValues.get('expires'), list):
            self.__keyValues['expires'] = self.__keyValues.get('expires')[0]

        if dict(self.__keyValues).has_key('uri') and isinstance(self.__keyValues.get('uri'), list):
            self.__keyValues['uri'] = self.__keyValues.get('uri')[0]

    def getValue(self, key):
        return self.__keyValues[key]

    #TODO: add locks to this method
    def add