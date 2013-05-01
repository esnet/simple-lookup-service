#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"type":["vp4"],"record-vp-domain":[ "ESnet"],"record-vp-locator":["nash-owamp.es.net"],"record-vp-name":["ESnet-nash Ping Responder"],"record-vp-type":[ "ping"]}' http://sowmya-dev-vm.es.net:8090/lookup/records
