#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"access-point":["http://localhost/accesspoint"],"client-uuid":["myuuid"],"service-type":["bwctl"],"domain":["es.net"]}' http://ps4.es.net:8085/lookup/services
