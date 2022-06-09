__author__ = 'student5'
import Record
from net.es.lookup.common.exception import RecordException


class ErrorRecord(Record):

    def __init__(self):
        super('error') #TODO: un-hardcode this

    def setErrorMessage(self, errorMessage):
        if errorMessage is not None:
            self.add('error-message', errorMessage) #TODO: un-hardcode this
        else:
            raise RecordException("Error message is null")

    def getErrorMessage(self):
        return str(self.getValue('error-message'))

    def setErrorCode(self, responseCode):
        self.add('error-code', responseCode)

    def getErrorCode(self):
        return int(self.getValue('error-code'))