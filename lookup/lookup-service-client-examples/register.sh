#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"record-service-domain":[ "ESnet"],"record-service-locator":["nash-owamp.es.net"],"record-service-name":["ESnet-nash Ping Responder"],"record-service-type":[ "ping"]}' http://localhost:8080/lookup/services

