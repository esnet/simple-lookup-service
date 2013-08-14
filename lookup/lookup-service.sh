#!/usr/bin/bash


printHelp ()
{
    echo "lookup-service.sh (start|stop) <options>"
    echo "Description: Starts/stops Simple lookup-service"
    echo "Options:"
    echo "-f           force an existing process to stop with a KILL signal"
    echo "-i           do mvn install, and deploy files"
    echo "-h           prints this help message"
    echo "-o <file>    output file for daemon STDOUT and STDERR. defaults to /dev/null"
    echo "-p <file>    the name of the pid file"
    echo "-t           run test site on different port"
    echo "Environment Variables:"
}
###############################################################################
APP_NAME="Simple lookup-service"
DEFAULT_PID_FILE="lookup-service.pid"

#Read command-line options
KILL_CMD="kill"
FORCE_KILL_CMD="kill -9"
READ_PID=0
READ_OUT_FILE=0
ACTION=""
PID_FILE="$DEFAULT_PID_FILE"
OUT_FILE="/dev/null"
USE_TEST=0
DO_INSTALL=""
for opt in $*
do
    if [ $READ_PID == 1 ]; then
        PID_FILE="$opt"
        READ_PID=0
    elif [ $READ_OUT_FILE == 1 ]; then
        OUT_FILE="$opt"
        READ_OUT_FILE=0
    elif [ "$opt" == "start" ] && [ -z "$ACTION" ]; then
        ACTION="start"
    elif [ "$opt" == "stop" ] && [ -z "$ACTION" ]; then
        ACTION="stop"
    elif [ "$opt" == "-o" ]; then
        READ_OUT_FILE=1
    elif [ "$opt" == "-f" ]; then
        KILL_CMD="$FORCE_KILL_CMD"
    elif [ "$opt" == "-p" ]; then
        READ_PID=1
    elif [ "$opt" == "-h" ]; then
        printHelp
        exit 0
    elif [ "$opt" == "-t" ]; then
        USE_TEST=1
    elif [ "$opt" == "-i" ]; then
        DO_INSTALL="1"
    else
        echo "Invalid option '$opt'"
        exit 0
    fi
done

if [ -n "$DO_INSTALL" ]; then 
    mvn clean
    mvn install
fi 

#default to start if ACTION is empty
if [ -z "$ACTION" ]; then
    ACTION="start"
fi

#Kill current process
PID=""
if [ -f "$PID_FILE" ]; then
    PID=`cat $PID_FILE`
fi
KILL_STATUS=0
if [ -n "$PID" ]; then
    $KILL_CMD $PID
    KILL_STATUS=$?
fi

#print error if kill failed during 'stop' otherwise forge ahead
# with start because likely error caused by process already being killed
if [ $KILL_STATUS != 0 ] && [ "$ACTION" == "stop" ]; then
    echo "Unable to stop $APP_NAME. Process may have already been killed."
    exit 1
elif [ -n "$PID" ] && [ $KILL_STATUS == 0 ] && [ "$KILL_CMD" != "$FORCE_KILL_CMD" ]; then
    echo "Waiting 5 seconds for old $APP_NAME processes to close..."
    sleep 5
    #make sure its really gone after giving it 5 seconds to die nicely
    `$FORCE_KILL_CMD $PID 2>/dev/null`
fi
if [ -f "$PID_FILE" ]; then
    rm $PID_FILE
fi

#If actions is stop then we are done
if [ "$ACTION" == "stop" ]; then
    echo "$APP_NAME stopped."
    exit 0
fi

#if action is start then continue....

BASEDIR="/opt/SimpleLookupService"
SOURCEDIR="lookup-service"
#copy files to webapp
if [ -n "$DO_INSTALL" ]; then 
    if [ ! -d "$BASEDIR" ]; then
        mkdir $BASEDIR
    fi
    mkdir $BASEDIR/log
    mkdir $BASEDIR/out
    mkdir $BASEDIR/process
    mkdir $BASEDIR/target
    cp -r $SOURCEDIR/etc/* $BASEDIR/etc
    cp $SOURCEDIR/target/lookup-service-0.0.1-SNAPSHOT.one-jar.jar $BASEDIR/target/
fi

cd $BASEDIR

# Start lookup-service daemon
if [ $USE_TEST -eq 1 ]; then
    nohup java -Xmx256m -Djava.net.preferIPv4Stack=true -jar target/lookup-service-0.0.1-SNAPSHOT.one-jar.jar --port=8081 > "$OUT_FILE" 2>&1 &
else
    nohup java -Xmx256m -Djava.net.preferIPv4Stack=true -jar target/lookup-service-0.0.1-SNAPSHOT.one-jar.jar > "$OUT_FILE" 2>&1 &
fi
echo $! > $PID_FILE
echo "$APP_NAME started.";

exit 0
