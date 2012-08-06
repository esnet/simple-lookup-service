#!/bin/sh

pidfile=$1
jarfile=$2
shift 2
vers="0.1"
shortname=lookup-service

if [ -z "$pidfile" ]; then
    DEFAULT_PID_DIR="./run"
    if [ ! -d "$DEFAULT_PID_DIR" ]; then
        mkdir "$DEFAULT_PID_DIR"
    fi
    pidfile=$DEFAULT_PID_DIR/${shortname}.pid
fi

if [ -z "$jarfile" ]; then
    jarfile=./target/${shortname}-0.0.1-SNAPSHOT.one-jar.jar
    echo "Starting ${shortname} with version:$vers"
fi

java -Djava.net.preferIPv4Stack=true -jar $jarfile $* &
echo $! > $pidfile