#!/bin/sh
curl -v -H "Content-Type: application/json" -X POST -d '{"type":["host1"],"record-vp-domain":[ "ESnet"],"record-vp-locator":["xyz11-owamp.es.net"],"record-vp-name":["ESnet-nash Ping Responder"],"record-vp-type":[ "ping"]}' http://localhost:8090/lookup/records
