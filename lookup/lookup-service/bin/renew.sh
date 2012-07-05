#!/bin/sh



curl -v -H "Content-Type: application/json" -X POST -d '{"client-uuid":["myuuid"],"record-ttl":"PT2H6M5S"}' http://localhost:8080/lookup/service/703ba377-0cdf-4ebf-8d86-8d3ec25e8203


