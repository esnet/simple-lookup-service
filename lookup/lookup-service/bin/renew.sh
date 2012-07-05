#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"client-uuid":["myuuid"],"record-ttl":"PT2H5M2S"}' http://localhost:8080/lookup/service/c179ddbe-f896-4712-8413-7e23aed2c780


