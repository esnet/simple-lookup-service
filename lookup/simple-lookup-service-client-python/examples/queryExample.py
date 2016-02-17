from sls_client.records import *
from sls_client.query import *
from sls_client.find_ps_ma import *

##query all  services
#queryString = 'type=services'
#response = query(queryString)
#print response

#for record in response:
#	print record[u'host-name']
	
#query host in a domain
queryString = 'type=host&group-domains=anl.gov'
response = query(queryString)

for record in response:
	print record[u'host-name']


#query for MA containing host data
#hostname = '202.179.252.18'
#result = get_ma_for_host(hostname)
#print result




