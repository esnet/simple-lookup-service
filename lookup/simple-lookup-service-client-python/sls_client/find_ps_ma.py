__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *
import sys
import json

from optparse import OptionParser


def get_ma_for_host(hostname):
    if(not hostname):
        raise Exception("Null Parameter", "Hostname parameter is empty.")

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

def get_json(query,hostlist):
    result={}
    result["ma-hosts"] = []
    result["search-query"] = query
    if(hostlist):
        result["ma-hosts"] = hostlist

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
                      choices=["console","json"],
                      default="console"
                      )
    (options, args) = parser.parse_args()

    if (not options.hostname):
        print "Please specify hostname"
        sys.exit(1)

    result = get_ma_for_host(options.hostname)
    if(options.output_type == "json"):
        output=get_json(options.hostname,result)
        print output
    else:
        for host in result:
            print host
if __name__=='__main__':
    main()