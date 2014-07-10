#!/bin/sh
# GET JSON record from sLS

curl -v -H "Content-Type: application/js" -X GET $*
