!/bin/sh
curl -v -H "Content-Type: application/json" -X POST -d '{"type":["service"],"group-domains":["es.net"],"record-vp-locator":["abc5-bwctl.es.net"],"record-vp-name":["ESnet BWCTL"],"record-vp-type":[ "bwctl315"]}' http://localhost:8090/lookup/records
