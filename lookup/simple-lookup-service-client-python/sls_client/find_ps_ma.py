__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *
import sys

from optparse import OptionParser


def get_ma_for_host(hostname):
    if(not hostname):
        raise Exception("Empty hostname", "Please specify parameter")

    response=[]
    queryKeys = ['psmetadata-dst-address','psmetadata-src-address']
    for queryKey in queryKeys:
        queryString = queryKey+"="+hostname
        response += query(queryString)

    result={}
    for record in response:
        malocators = record[u'psmetadata-ma-locator']
        for malocator in malocators:
            malocatorString = malocator.encode("ascii")
            result[malocatorString] = 1

    return result.keys()

def main():

    parser = OptionParser()

    parser.add_option("-n", "--hostname",
                      dest="hostname",
                      help="specify hostname or IP")
    (options, args) = parser.parse_args()

    if (not options.hostname):
        print "Please specify hostname"
        sys.exit(1)

    result = get_ma_for_host(options.hostname)
    print result
if __name__=='__main__':
    main()