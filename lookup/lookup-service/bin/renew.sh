#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"client-uuid":["myuuid"]}' http://localhost:8080/lookup/service/d3e639e4-2a38-45da-ad8c-204202cd72c3
