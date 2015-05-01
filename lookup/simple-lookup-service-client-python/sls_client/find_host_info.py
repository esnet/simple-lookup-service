__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *
import sys
import json

from optparse import OptionParser

HOST_HW_PROCESSORCOUNT = 'host-hardware-processorcount'
HOST_HW_PROCESSORCORE = 'host-hardware-processorcore'
HOST_HW_PROCESSORSPEED = 'host-hardware-processorspeed'
HOST_HW_MEMORY = 'host-hardware-memory'

HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV='host-net-tcp-autotunemaxbuffer-recv'
HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND='host-net-tcp-autotunemaxbuffer-send'
HOST_NET_TCP_MAXBUFFER_RECV='host-net-tcp-autotunemaxbuffer-recv'
HOST_NET_TCP_MAXBUFFER_SEND='host-net-tcp-autotunemaxbuffer-send'
HOST_NET_TCP_CONGESTIONALGORITHM='host-net-tcp-congestionalgorithm'

HOST_OS_VERSION = 'host-os-version'
HOST_OS_NAME = 'host-os-name'
HOST_OS_KERNEL = 'host-os-kernel'


def get_host_info(hostname):

    response=[]
    queryKey = 'interface-addresses'
    queryString = queryKey+"="+hostname
    response += query(queryString)

    uris=[]
    for record in response:
        uris.append(record[u'uri'])

    hostQueryKey='host-net-interfaces'
    hostResponse=[]
    for uri in uris:
        hostQuery=hostQueryKey+"="+uri
        hostResponse += query(hostQuery)

    hostRecords=[]

    for host in hostResponse:
        hostRecord={}
        hostRecord[HOST_HW_MEMORY]=host[HOST_HW_MEMORY]
        hostRecord[HOST_HW_PROCESSORCORE]=host[HOST_HW_PROCESSORCORE]
        hostRecord[HOST_HW_PROCESSORCOUNT]=host[HOST_HW_PROCESSORCOUNT]
        hostRecord[HOST_HW_PROCESSORSPEED]=host[HOST_HW_PROCESSORSPEED]

        hostRecord[HOST_OS_NAME]=host[HOST_OS_NAME]
        hostRecord[HOST_OS_KERNEL]=host[HOST_OS_KERNEL]
        hostRecord[HOST_OS_VERSION]=host[HOST_OS_VERSION]

        hostRecords.append(hostRecord)

    return hostResponse

def get_host_info_json(hostname):
    hostlist = get_host_info(hostname)
    result={}
    result["host"] = []
    result["search-query"] = hostname
    if(hostlist):
        result["host"] = hostlist

    json_output = json.dumps(result)
    return json_output


def main():

    parser = OptionParser()

    parser.add_option("-n", "--hostname",
                      dest="hostname",
                      help="specify hostname or IP")
    parser.add_option("-o", "--output",
                      dest="output_type",
                      help="output type - json or console",
                      choices=["console","json","list"],
                      default="console"
    )
    (options, args) = parser.parse_args()

    if (not options.hostname):
        print "Please specify hostname"
        sys.exit(1)

    if(options.output_type=="json"):
        print get_host_info_json(options.hostname)
    elif(options.output_type=="console"):
        result=get_host_info(options.hostname)
        for host in result:
            print "\n***** HOST HARDWARE *****"
            print "\n"+HOST_HW_PROCESSORCORE+":",
            for core in host[HOST_HW_PROCESSORCORE]:
                print core

            print "\n"+HOST_HW_PROCESSORSPEED+":",
            for speed in host[HOST_HW_PROCESSORSPEED]:
                print speed

            print "\n"+HOST_HW_PROCESSORCOUNT+":",
            for count in host[HOST_HW_PROCESSORCOUNT]:
                print count

            print "\n"+HOST_HW_MEMORY+":",
            for memory in host[HOST_HW_MEMORY]:
                print memory


            print "\n***** HOST OS *****"
            print "\n"+HOST_OS_NAME+":",
            for osname in host[HOST_OS_NAME]:
                print osname

            print "\n"+HOST_OS_VERSION+":",
            for osversion in host[HOST_OS_VERSION]:
                print osversion

            print "\n"+HOST_OS_KERNEL+":",
            for oskernel in host[HOST_OS_KERNEL]:
                print oskernel


            print "\n***** HOST NETWORKING *****"
            print "\n"+HOST_NET_TCP_CONGESTIONALGORITHM+":",
            for alg in host[HOST_NET_TCP_CONGESTIONALGORITHM]:
                print alg

            print "\n"+HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV+":",
            for buf in host[HOST_NET_TCP_AUTOTUNEMAXBUFFER_RECV]:
                print buf

            print "\n"+HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND+":",
            for buf in host[HOST_NET_TCP_AUTOTUNEMAXBUFFER_SEND]:
                print buf

            print "\n"+HOST_NET_TCP_MAXBUFFER_RECV+":",
            for buf in host[HOST_NET_TCP_MAXBUFFER_RECV]:
                print buf

            print "\n"+HOST_NET_TCP_MAXBUFFER_SEND+":",
            for buf in host[HOST_NET_TCP_MAXBUFFER_SEND]:
                print buf

    else:
        print get_host_info(options.hostname)

if __name__=='__main__':
    main()