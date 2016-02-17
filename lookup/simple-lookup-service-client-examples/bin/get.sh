#!/bin/sh
# GET record from sLS

curl -v -H "Content-Type: application/json" -X GET $* 
