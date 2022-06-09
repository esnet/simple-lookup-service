import time
from net.es.lookup.common.exception.LSClientException import LSClientException
import socket
import httplib


__author__ = 'student5'


class SimpleLS(object):

    __protocol = 'http'
    __connectionUrl = None
    __host = 'localhost'
    __port = 8090
    __relativeUrl = None
    __connectionType = None
    __status = 'unknown'
    __latency = 0
    __data = None
    __timeout = 5000
    __response = None
    __responseCode = None
    __errorMessage = None

    def __init__(self, host, port, connectionType='GET'):
        if host:
            self.host = host
        else:
            raise LSClientException("Invalid host name")

        self.port = port

        self.__connectionType = self.__createAbsoluteUrl("")

        if self.__isValidConnectionType(connectionType):
            self.__connectionType = connectionType
        else:
            raise LSClientException("Invalid connection type")

    def getTimeout(self):
        return self.__timeout

    #TODO: the check is to restrict the type of Timeout; is that necessary?
    def setTimeout(self, timeout):
        if timeout >= 0:
            self.__timeout = timeout
        else:
            raise LSClientException("Timeout must be a positive integer")

    def getResponse(self):
        return self.__response;

    def getResponseCode(self):
        return self.__responseCode

    def getErrorMessage(self):
        return self.__errorMessage

    def getHost(self):
        return self.__host

    def getRelativeUrl(self):
        return self.__relativeUrl

    #TODO: add lock to this method
    def setRelativeUrl(self, relativeUrl):
        if relativeUrl is not None:
            self.__relativeUrl = relativeUrl
            self.__connectionUrl = self.__createAbsoluteUrl(relativeUrl)
        else:
            raise LSClientException("Empty parameter in setRelativeUrl")

    #TODO: does this method need to check for appropriate url syntax (like the java client does)?
    def __createAbsoluteUrl(self, relativeUrl):
        return str(self.__protocol + '://' + self.__host + ':' + self.__port + '/' + relativeUrl)

    def getConnectionUrl(self):
        return self.__connectionUrl

    def getConnectionType(self):
        return self.__connectionType

    def setConnectionType(self, connectionType):
        if self.__isValidConnectionType(connectionType):
            self.__connectionType = connectionType
        else:
            raise LSClientException("Invalid connection type")

    def getStatus(self):
        return self.__status

    def getLatency(self):
        return self.__latency

    def getData(self):
        return self.__data

    #TODO: should this check for None or empty first?
    def setData(self, data):
        self.__data = data

    #TODO: add lock to this method
    #TODO: un-hardcode the status values
    def connect(self):
        s = socket.socket(self.__host, self.__port)
        s.settimeout(5.0)
        try:
            lat = time.time(s.connect)
            reachable = True
        except socket.error:
            reachable = False
        if reachable:
            self.__latency = lat
            self.__status = 'alive'
        else:
            self.__latency = 0
            self.__status = 'unreachable'

    #TODO: add lock to this method
    def send(self):
        httpclient = httplib.HTTPConnection(self.__host, self.__port)
        headers = { "Accept" : "application/json", "Content-type" : "application/json" }

        if str(self.__connectionType).lower() == 'get':
            httpclient.request('GET', self.__connectionUrl, '', headers)
            try:
                httpResponse = httpclient.getresponse(True) #TODO: is buffering=True correct?
            except httplib.HTTPException, e:
                raise LSClientException(e.message)
        elif str(self.__connectionType).lower() == 'post':
            httpclient.request('POST', self.__connectionUrl, self.__data, headers)
            #TODO: not checking the data first like we did in java client
            try:
                httpResponse = httpclient.getresponse(True)
            except httplib.HTTPException, e:
                raise LSClientException(e.message)
        elif str(self.__connectionType).lower() == 'delete':
            httpclient.request('DELETE', self.__connectionUrl)
            try:
                httpResponse = httpclient.getresponse(True)
            except httplib.HTTPException, e:
                raise LSClientException(e.message)
        else:
            raise LSClientException("Cannot establish connection. Invalid connection type")

        self.__responseCode = httpResponse.status
        self.__errorMessage = httpResponse.reason
        self.__response = httpResponse.read()

    def __isValidConnectionType(self, connectionType):

        if str.lower(connectionType) in ('get', 'post', 'delete'):
            return True
        else:
            return False
