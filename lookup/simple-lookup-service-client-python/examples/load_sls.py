from sls_client.records import *
from sls_client.query import *
import requests
import json

	
#query host in a domain
queryString = ''
response = query(queryString)


for record in response:
	print json.dumps(record)
	r = requests.post("http://localhost:8090/lookup/records", data = json.dumps(record))
