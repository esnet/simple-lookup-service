#!/usr/bin/env python

"""
sls_dig - client to fetch host information from sLS
"""
__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *
import sls_client.find_host_info
from optparse import OptionParser
import sys

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

HOST_INTERFACE='host-net-interfaces'

HOST_ADMINISTRATORS='host-administrators'

LOCATION_CITY='location-city'
LOCATION_STATE='location-state'
LOCATION_COUNTRY='location-country'
LOCATION_SITE='location-sitename'

PERSON_NAME='person-name'
PERSON_EMAILS='person-emails'


INTERFACE_ADDRESS = 'interface-addresses'
INTERFACE_NAME='interface-name'


#TODO: Add location information, admin
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
        print sls_client.find_host_info.get_host_info_json(options.hostname)
    elif(options.output_type=="console"):
        result = sls_client.find_host_info.get_host_info(options.hostname)
        for host in result:
            interface=host[HOST_INTERFACE]
            print "\n***** HOST INTERFACE ADDRESS *****"
            if (interface):
                print "\n"+ INTERFACE_ADDRESS+":",
                for ip in interface[INTERFACE_ADDRESS]:
                    print ip,

                print "\n"+ INTERFACE_NAME+":",
                for ifname in interface[INTERFACE_NAME]:
                    print ifname,

            print "\n"
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


            print "\n***** HOST LOCATION *****"
            print "\n"+LOCATION_SITE+":",
            for site in host[LOCATION_SITE]:
                print site

            print "\n"+LOCATION_CITY+":",
            for city in host[LOCATION_CITY]:
                print city

            print "\n"+LOCATION_STATE+":",
            for state in host[LOCATION_STATE]:
                print state

            print "\n"+LOCATION_COUNTRY+":",
            for country in host[LOCATION_COUNTRY]:
                print country

            print "\n***** HOST ADMIN *****"
            admins=host[HOST_ADMINISTRATORS]
            for admin in admins:
                print "\n"+PERSON_NAME+":",
                for name in admin[PERSON_NAME]:
                    print name

                print "\n"+PERSON_EMAILS+":",
                for name in admin[PERSON_EMAILS]:
                    print name

                print "\n"+LOCATION_CITY+":",
                for city in admin[LOCATION_CITY]:
                    print city

                print "\n"+LOCATION_STATE+":",
                for state in admin[LOCATION_STATE]:
                    print state

                print "\n"+LOCATION_COUNTRY+":",
                for country in admin[LOCATION_COUNTRY]:
                    print country

    else:
        print sls_client.find_host_info.get_host_info(options.hostname)

if __name__=='__main__':
    main()