#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"access-point":["http://localhost/accesspoint1"],"client-uuid":["myuuid"],"service-type":["bwctl"],"domain":["LHC","es.net"]}' http://localhost:8080/lookup/services
