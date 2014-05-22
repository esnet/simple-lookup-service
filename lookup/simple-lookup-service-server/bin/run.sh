#!/bin/bash
java -Xmx256M -agentpath:/home/sowmya/jprofiler/bin/linux-x64/libjprofilerti.so=port=8849,nowait  -jar target/simple-lookup-service-server-1.1.one-jar.jar $*
