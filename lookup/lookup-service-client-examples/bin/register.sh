#!/bin/sh
curl -v -H "Content-Type: application/json" -X POST -d '{"type":["host1"],"group-domains":[ "es1.net"],"record-vp-locator":["xyz-owamp.es.net"],"record-vp-name":["ESnet-nash Ping Responder"],"record-vp-type":[ "ping"]}' http://localhost:8090/lookup/records
