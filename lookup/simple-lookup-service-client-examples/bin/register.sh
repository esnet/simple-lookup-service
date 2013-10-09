!/bin/sh
curl -v -H "Content-Type: application/json" -X POST -d '{"type":["host"],"group-domains":["es.net"],"record-vp-locator":["abc9-bwctl.es.net"],"record-vp-name":["ESnet BWCTL"],"record-vp-type":[ "bwctl315"]}' http://antg-test-vm.es.net:8090/lookup/records
