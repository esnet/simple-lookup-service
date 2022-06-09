#!/bin/sh
# GET record from sLS

curl -v -H -X GET $1 &
curl -v -H -X GET $2 &
curl -v -H -X GET $3 &
curl -v -H -X GET $4 &
curl -v -H -X GET $5


#sh getMany.sh http://127.0.0.1:8090/lookup/client-test/5b0f995d-2641-4390-aca7-d047f1027cd9 http://127.0.0.1:8090/lookup/client-test/42702563-6b04-4d2a-8bf5-108688bcaf2d http://127.0.0.1:8090/lookup/client-test/42702563-6b04-4d2a-8bf5-108688bcaf2d http://127.0.0.1:8090/lookup/client-test/21ddfd33-b8e6-4c1f-9e63-99e94b64d887 http://127.0.0.1:8090/lookup/client-test/21ddfd33-b8e6-4c1f-9e63-99e94b64d887