__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *
import json
import socket

HOST_NAME='host-name'
GROUP_DOMAINS='group-domains'
RECORD_TYPE = 'record-type'

def search_domains(domain,record_type):

    if(not domain):
        raise Exception("Null Parameter", "Domain is empty.")

    #Try hostname. If it fails, try host name for interface. If that also fails, finally do dns resolution
    queryString = GROUP_DOMAINS+"="+hostname
    if record_type:
        queryString = "&"+RECORD_TYPE+"="+record_type
    response = query(queryString)
    hosts=[]
    if response:
        for record in response:
            hosts.append(record['host-name'])

    for host in hosts:
        admin = __get_admin_info(host)
        host[HOST_ADMINISTRATORS] = admin

    return hosts


def get_host_info_json(domain,record_type):
    hostlist = search_domains(hostname)
    result={}
    result["host"] = []
    result["search-query"] = GROUP_DOMAINS+"="+hostname+ "&"+RECORD_TYPE+"="+record_type
    if(hostlist):
        result["host"] = hostlist

    json_output = json.dumps(result)
    return json_output
