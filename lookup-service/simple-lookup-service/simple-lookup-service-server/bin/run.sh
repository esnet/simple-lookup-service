#! /bin/sh
java -Xmx512M  -jar target/simple-lookup-service-server-1.1-SNAPSHOT.one-jar.jar $* > lookup-service.out 2>&1 &
