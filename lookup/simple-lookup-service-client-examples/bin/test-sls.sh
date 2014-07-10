#!/bin/bash
#
# This script test the basic functionnalities any sLS should have
# Give it a single argument: the base URL to the sLS
# Ex:
#       test-sls.sh http://uat-sls.geant.net:8090

if [[ $1 != "" ]]; then
    sls=$1
else
    echo "Give me the base URL of the sLS to test"
    exit 1
fi
CURL='curl'
HEADER='Content-Type: application/json'
record='record.json'

# Registring a new entry
echo "Registring a new entry ..."
${CURL} -H "${HEADER}" -X POST -d @${record} ${sls}/lookup/records/
echo -e "\n"

# Getting the key from the entry
echo "Getting key and TTL from new entry ..."
key=`${CURL} -sS -X GET ${sls}/lookup/records/ | perl -aF'},{' -ne 'for (@F) { /:\["Test-if-sLS-working"\],.*"uri":"(.*?[^\\\])"/ && print "$1\n" }'`
echo -e "Key is: \033[1m$key\033[0m"
expires=`${CURL} -sS -X GET ${sls}/lookup/records/ | perl -aF'},{' -ne 'for (@F) { /:\["Test-if-sLS-working"\],.*"expires":"(.*?[^\\\])"/ && print "$1\n" }'`
echo -e "Expiration date is: \033[1m$expires\033[0m"
echo

# Getting the entry from the key
echo -e "Retreiving entry from key ... (must return \033[1m200\033[0m)"
${CURL} -I -X GET ${sls}/${key}
# TODO: compare with registered data?

# Renewing registration
echo -e "Renew registration ... (must return \033[1m200\033[0m)"
${CURL} -I -X POST ${sls}/${key}

# Deleting registration
echo -e "Delete registration ... (must return \033[1m200\033[0m)"
${CURL} -I -X DELETE ${sls}/${key}
# Check doesn't exist anymore
echo -e "Checking if entry is deleted ... (must return \033[1m404\033[0m)"
${CURL} -I -X GET ${sls}/${key}

# TODO
# Testing updates/modifications?
# Testing expiration
