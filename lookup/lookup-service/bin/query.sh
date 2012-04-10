#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"service-type":"bwctl"}' http://localhost:8080/lookup/query
