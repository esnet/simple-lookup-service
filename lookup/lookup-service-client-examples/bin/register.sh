#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"record-type":["non-vp"],"record-vp-domain":[ "ESnet"],"record-vp-locator":["nash-owamp.es.net"],"record-vp-name":["ESnet-nash Ping Responder"],"record-vp-type":[ "ping"]}' http://localhost:8080/lookup/services

