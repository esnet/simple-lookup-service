#!/usr/bin/env python
from elasticsearch import Elasticsearch
from elasticsearch.client import IlmClient
from elasticsearch import exceptions
from datetime import datetime

es = Elasticsearch([{'host':'localhost','port':9200}])
index="lookup"
ilm_policy_id="perfsonar-ls-snapshot-policy"
perfsonar_policy_json='{"policy":{"_meta":{"description": "used for perfsonar ls data"}, "phases":{"delete":{"min_age": "3650d","actions": {"delete": {}}}}}}'
str_date_time = datetime.now().strftime("%Y-%m-%d-%H-%M-%S")
dest_index = "ls-snapshot"+"-"+str_date_time

src_index=index

ilm = IlmClient(es)
try:
  res=ilm.get_lifecycle(ilm_policy_id)
  print("Policy found so continuing")
except exceptions.NotFoundError as e:
  print("Policy not found so installing")
  try:
    ilm.put_lifecycle(ilm_policy_id, perfsonar_policy_json)
    print("installed policy")
  except e:
    print("Policy not installed. Install again")
    print(str(e))
#print(res)

dest_settings = {"index.number_of_replicas": 0,
    "index.lifecycle.name": ilm_policy_id}

src_mapping_res = es.indices.get_mapping(index=src_index)
src_mapping = src_mapping_res["lookup"]["mappings"]
es.indices.create(index=dest_index)
es.indices.put_mapping(index=dest_index, body=src_mapping)

es.reindex({
"conflicts": "proceed",
 "source":{
   "index": "lookup"
 },
 "dest":{
   "index": dest_index
 }
}, wait_for_completion=True, request_timeout=300)

try:
  es.indices.put_settings(index=dest_index, body={
    "index.number_of_replicas": 0,
    "index.lifecycle.name": ilm_policy_id
  }, ignore=[400])
except exceptions.NotFoundError as e:
  print("Index not found so not applying settings")