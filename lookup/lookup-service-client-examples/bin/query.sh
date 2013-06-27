#!/bin/sh

curl -v -H "Content-Type: application/js" -X GET http://localhost:8090/lookup/records/?type=vp
