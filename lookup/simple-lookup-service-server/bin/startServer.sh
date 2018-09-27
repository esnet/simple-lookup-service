#!/bin/sh

pidfile=$1
jarfile=$2
shift 3
vers="2.0"
shortname=lookup-service

#include variables
currdir="$(dirname "$0")"
if [ -f "${currdir}/lookup-service.env" ]; then
    source ${currdir}/maddash-server.env
fi

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

#set java opts
full_java_opts=$JAVA_OPTS
if [ -n "$JAVA_MAX_HEAP" ]; then
    full_java_opts="-Xmx${JAVA_MAX_HEAP} $full_java_opts"
fi

java $full_java_opts -jar $jarfile $* &
echo "Starting ${shortname} with $*"
echo $! > $pidfile
