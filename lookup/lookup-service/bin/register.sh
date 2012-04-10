#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"access-point":"http://localhost/accesspoint","client-uuid":"myuuid","service-type":"bwctl","domain":"es.net","ttl":"P0Y0M0DT2H0M0S"}' http://localhost:8080/lookup/services
