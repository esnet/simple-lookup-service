#!/bin/sh

<<<<<<< .mine

curl -v -H "Content-Type: application/json" -X POST -d '{"record-type":["service"],"record-service-locator":["http://localhost/accesspoint1000"],"record-privatekey":["myuuid"],"record-service-type":["owamp"],"record-service-domain":["LHC","es.net"]}' http://localhost:8080/lookup/services
=======
curl -v -H "Content-Type: application/js" -X POST -d '{"record-type":["service"],"record-service-locator":["http://localhost/accesspoint1010101010"],"record-privatekey":["myuuid"],"record-service-type":["owamp"],"record-service-domain":["LHC","es.net"]}' http://localhost:8080/lookup/services
>>>>>>> .r275
