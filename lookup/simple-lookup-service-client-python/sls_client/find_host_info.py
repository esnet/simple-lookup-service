__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *
import json
import socket

HOST_NAME='host-name'
HOST_INTERFACE='host-net-interfaces'
INTERFACE_ADDRESS='interface-addresses'
RECORD_URI = 'uri'
INTERFACE_NAME='interface-name'
HOST_ADMINISTRATORS = 'host-administrators'

def get_host_info(hostname):

    if(not hostname):
        raise Exception("Null Parameter", "Hostname parameter is empty.")

    #Try hostname. If it fails, try host name for interface. If that also fails, finally do dns resolution
    queryString = HOST_NAME+"="+hostname
    response = query(queryString)
    hosts=[]
    if response:
        uris=__get_interface_uris_for_host(response)

        interfaces = __get_interfaces(uris)
        #get interfaces
        for result in response:
            result[HOST_INTERFACE]=[]
            for interface in interfaces:
                result[HOST_INTERFACE].append(interface)

        hosts=response

    else:
        queryString = INTERFACE_ADDRESS+"="+hostname
        response = query(queryString)

        if response:
            hosts = __get_hosts_for_interfaces(response)
        else:
            #DNS resolution
            ipaddr = socket.getaddrinfo(hostname, None)
            ipaddresses={}
            #ipaddr is a 5-tuple and the ipv4 or ipv6 is the last element in the tuple.
            for ip in ipaddr:
                if(ip[4][0]):
                    ipaddresses[ip[4][0]]=1

            ipaddresses[hostname]=1
            hosts=[]
            for ip in ipaddresses:
                queryString = INTERFACE_ADDRESS+"="+ip
                response = query(queryString)

                if response:
                    hosts += __get_hosts_for_interfaces(response)


    for host in hosts:
        admin = __get_admin_info(host)
        host[HOST_ADMINISTRATORS] = admin

    return hosts


def __get_interface_uris_for_host(list_of_host):
    uris={}
    for host in list_of_host:
        for interface in host[HOST_INTERFACE]:
            uris[interface]=1
    return uris

def __get_hosts_for_interfaces(list_of_interfaces):
    hostResponse=[]
    for interface in list_of_interfaces:
        uri = interface[RECORD_URI]
        hostQuery=HOST_INTERFACE+"="+uri
        response = query(hostQuery)
        for host in response:
            host[HOST_INTERFACE] = interface

        hostResponse+=response

    return hostResponse

def __get_admin_info(host):
    adminUris = host[HOST_ADMINISTRATORS]
    response=[]
    if(adminUris):
        for adminUri in adminUris:
            queryString = RECORD_URI+"="+adminUri
            response += query(queryString)
        return response


def __get_interfaces(list_of_uris):
    interfaceResponse = []
    for uri in list_of_uris:
        interfaceQuery= RECORD_URI+'='+uri
        interfaceResponse += query(interfaceQuery)
    return interfaceResponse

def get_host_info_json(hostname):
    hostlist = get_host_info(hostname)
    result={}
    result["host"] = []
    result["search-query"] = hostname
    if(hostlist):
        result["host"] = hostlist

    json_output = json.dumps(result)
    return json_output
