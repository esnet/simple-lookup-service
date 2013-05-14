#!/usr/bin/bash
mvn install -DskipTests
cd lookup-service
java -jar target/lookup-service-0.1.one-jar.jar
