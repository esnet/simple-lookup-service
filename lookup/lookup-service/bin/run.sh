#!/bin/bash
java -Xmx256m -Djava.net.preferIPv4Stack=true -jar target/lookup-service-0.1.one-jar.jar $*
