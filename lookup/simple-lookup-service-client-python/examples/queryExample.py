from sls_client.records import *
from sls_client.query import *
import sls_client.find_ps_ma

#query all  services
queryString = 'type=service'
response = query(queryString)
print response

#query for MA containing host data
hostname = '202.179.252.18'
result = find_ps_ma.get_ma_for_host(hostname)
print result




