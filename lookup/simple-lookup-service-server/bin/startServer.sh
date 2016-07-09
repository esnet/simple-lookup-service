#!/bin/sh

pidfile=$1
jarfile=$2
memory=$3
shift 3
vers="2.0"
shortname=lookup-service

if [ -z "$pidfile" ]; then
    DEFAULT_PID_DIR="./run"
    if [ ! -d "$DEFAULT_PID_DIR" ]; then
        mkdir "$DEFAULT_PID_DIR"
    fi
    pidfile=$DEFAULT_PID_DIR/${shortname}.pid
fi

if [ -z "$jarfile" ]; then
    jarfile=./target/${shortname}-${vers}.one-jar.jar
    echo "Starting ${shortname} with version:$vers"
fi

java $memory -jar $jarfile $* &
echo "Starting ${shortname} with $*"
echo $! > $pidfile
