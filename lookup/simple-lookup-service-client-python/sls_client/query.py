from operator import itemgetter
import urllib
from urlparse import parse_qs

import requests

_concurrency_enabled=False
try:
    import concurrent.futures
    _MAX_CONCURRENT_REQUESTS = 4
    _concurrency_enabled = True
except ImportError:
    pass

_LS_HINTS = "http://ps-west.es.net:8096/lookup/activehosts.json"

def get_hosts():
    hosts = requests.get(_LS_HINTS).json().get("hosts", [])
    hosts = sorted(hosts, key=lambda v: v.get("priority", ""), reverse=True)
    for host in hosts:
        if host.get("status", "") != "alive" or not host.get("locator", ""):
            hosts.remove(host)
    return hosts

def refresh_hosts():
    _ls_hosts = get_hosts()

_ls_hosts = get_hosts()

def query(query="", hosts=_ls_hosts):
    try:
        query = hash_to_query(query)
    except:
        pass
    records = []
    urls = [(host["locator"] + "?" + query) for host in hosts]
    if _concurrency_enabled:
        with concurrent.futures.ThreadPoolExecutor(max_workers=_MAX_CONCURRENT_REQUESTS) as pool:
            for response in pool.map(get_url, urls):
                if response is None:
                    continue
                ls_host = response.url.split("lookup/records")[0]
                for record in response.json():
                    record["ls-host"] = ls_host
                    records.append(record)
    else:
        for url in urls:
            response = get_url(url)
            if response is None:
                continue
            ls_host = response.url.split("lookup/records")[0]
            for record in response.json():
                if record.get("uri", "") and record.get("type", [ False ])[0]:
                    record["ls-host"] = ls_host
                    records.append(record)
    return records
    
def hash_to_query(query_hash={}):
    query = urllib.urlencode(sorted(query_hash.items(), key=lambda v: v[0]), True)
    return query

def query_to_hash(query=""):
    query_hash = parse_qs(query)
    return query_hash

def get_url(url):
    try:
        return requests.get(url)
    except:
        pass
    return None
