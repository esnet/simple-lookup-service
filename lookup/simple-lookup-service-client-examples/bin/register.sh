#!/bin/sh
curl -v -H "Content-Type: application/json" -X POST -d '{"type":["host"],"group-domains":["es.net"],"record-vp-locator":["abc5-bwctl.es.net"],"record-vp-name":["ESnet BWCTL"],"record-vp-type":[ "bwctl85"]}' http://sowmya-dev-vm.es.net:8090/lookup/records
