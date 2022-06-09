from sls_client.records import *
from sls_client.query import *

queryString = 'type=host'
response = query(queryString)

queryPattern=u'es.net'
count=0

result=[]
for record in response:
    record.pop(u'ls-host')
    for key in record:
        if queryPattern in record[key]:
            result.append(record)
            count += 1

print count
