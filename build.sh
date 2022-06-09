#!/bin/sh
#for travis build
cd lookup-service
mvn clean package -DskipTests
#requires a working LS instance to run the tests
#So check only if package builds correctly

