#!/usr/bin/env python

"""
find_ps_ma - finds the MA that has test data for specified host
"""
__author__ = 'sowmya'
import  sls_client.records
import sls_client.query
import sls_client.find_ma
import sys
from optparse import OptionParser




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
        print sls_client.find_ma.get_ma_for_host_json(options.hostname)
    elif(options.output_type=="console"):
        result=sls_client.find_ma.get_ma_for_host(options.hostname)
        for ma in result:
            print ma
    else:
        print sls_client.find_ma.get_ma_for_host(options.hostname)

if __name__=='__main__':
    main()
