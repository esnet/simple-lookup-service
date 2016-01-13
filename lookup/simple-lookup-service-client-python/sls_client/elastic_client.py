__author__ = 'sowmya'

from elasticsearch import Elasticsearch
from elasticsearch.helpers import bulk, streaming_bulk
from sls_client.query import *
from sls_client.records import *

def get_record():
	queryString = ''
	response = query(queryString)
	for record in response:
		yield record

es = Elasticsearch([{'host':'perfsonar-dev.es.net'}])
index="perfsonar"
es.create( index=index,
        doc_type='perfsonar_records',
        id='ps_records',
        body={},ignore=409)


for ok, result in streaming_bulk(
        es,
        get_record(),
        index=index,
        doc_type='records',
        chunk_size=50 # keep the batch sizes small for appearances only
    ):
    action, result = result.popitem()
    doc_id = '/%s/records/%s' % (index, result['_id'])
    # process the information from ES whether the document has been
    # successfully indexed
    if not ok:
        print('Failed to %s document %s: %r' % (action, doc_id, result))
    else:
        print(doc_id)



