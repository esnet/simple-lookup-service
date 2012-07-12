#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"record-type":["vp"],"record-vp-name":["somename"]}' http://localhost:8080/lookup/services

