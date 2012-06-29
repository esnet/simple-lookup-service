#!/bin/sh



curl -v -H "Content-Type: application/json" -X POST -d '{"client-uuid":["myuuid"],"record-ttl":2}' http://localhost:8080/lookup/service/9a113c60-1f28-4e3a-9b7b-073955d84219


