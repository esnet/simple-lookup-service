#!/bin/sh

i=1
while [ "$i" -le "10000" ];
do
   rec='{"client-test-key1":["value1"],"type":["client-test"],"client-test-key2":["value2"],"client-test-key3":["value3","value32"],"client-test-name":["Test1"],"client-test-key4":["value4"],"client-test-value5":'
    data="$rec['$i']}"
    echo $data
    curl -v -H "Content-Type: application/json" -X POST -d $data $* 
    let i=i+1
done
