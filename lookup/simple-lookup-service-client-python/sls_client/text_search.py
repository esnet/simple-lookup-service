__author__ = 'sowmya'

from sls_client.records import *
from sls_client.query import *

import json

def search(record,text):

    queryRecord = 'type='+record
    response = query(queryRecord)

    queryPattern=''+text

    result=[]
    for record in response:
        if u'ls-host' in record:
            record.pop(u'ls-host')
        for key in record:
            if queryPattern in record[key]:
                result.append(record)
    
    return result


def search_tojson(record,text):
    response = search(record,text)
    json_response = json.dumps(response)
    return json_response
