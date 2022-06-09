__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *
import json
import socket



def get_ma_for_host(hostname):

    ipaddresses={}
    if(not hostname):
        raise Exception("Null Parameter", "Hostname parameter is empty.")
    ipaddr = socket.getaddrinfo(hostname, None)

    ipaddresses[hostname]=1

    #ipaddr is a 5-tuple and the ipv4 or ipv6 is the last element in the tuple.
    for ip in ipaddr:
        if(ip[4][0]):
            ipaddresses[ip[4][0]]=1

    response=[]
    result={}
    queryKeys = ['psmetadata-dst-address','psmetadata-src-address']
    for queryKey in queryKeys:
        for ip in ipaddresses:
            queryString = queryKey+"="+ip
            response += query(queryString)


    for record in response:
        malocators = record[u'psmetadata-ma-locator']
        for malocator in malocators:
            malocatorString = malocator.encode("ascii")
            result[malocatorString] = 1

    return result.keys()

def get_ma_for_host_json(hostname):
    hostlist = get_ma_for_host(hostname)
    result={}
    result["ma-hosts"] = []
    result["search-query"] = hostname
    if(hostlist):
        result["ma-hosts"] = hostlist

    json_output = json.dumps(result)
    return json_output