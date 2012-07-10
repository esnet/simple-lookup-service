#!/bin/sh

curl -v -H "Content-Type: application/json" -X POST -d '{"client-uuid":["myuuid"],"record-ttl":"PT2H5M2S"}' http://localhost:8080/lookup/service/1421df37-b944-481e-ab00-7ee95a3b0e21


