#!/bin/sh

curl -v -H "Content-Type: application/js" -X GET http://localhost:8080/lookup/services/?record-type=vp
