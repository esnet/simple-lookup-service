#!/bin/sh
curl -v -H "Content-Type: application/json" -X POST -d '{"type":["host"],"group-domains":["es.net"],"record-vp-locator":["abc-bwctl.es.net"],"record-vp-name":["ESnet-nash Ping Responder"],"record-vp-type":[ "bwctl3"]}' http://sowmya-dev-vm.es.net:8090/lookup/records

