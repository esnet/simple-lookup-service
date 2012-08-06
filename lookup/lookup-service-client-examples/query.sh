#!/bin/sh

curl -v -H "Content-Type: application/js" -X GET http://localhost:8080/lookup/services/?record-service-domain=ES*,L*\&record-service-locator=wash-owamp.es.net\&record-service-type=ping\&record-type=service\&record-operator=any

