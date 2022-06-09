#!/bin/sh
# Update record in sLS

curl -v -H "Content-Type: application/json" -X POST $*
