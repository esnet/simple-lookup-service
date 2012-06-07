#!/bin/sh

curl -v -H "Content-Type: application/json" -X GET http://localhost:8080/lookup/services/?domain=es.net,LHC\&domain-operator=any

