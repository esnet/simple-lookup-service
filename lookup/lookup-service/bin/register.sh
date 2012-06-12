#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"record-service-locator":["http://localhost/accesspoint1"],"record-privatekey":["myuuid"],"record-service-type":["bwctl"],"record-service-domain":["LHC","es.net"]}' http://localhost:8080/lookup/services
