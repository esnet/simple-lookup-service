#!/usr/bin/bash

APP_NAME="Simple Lookup Service"
VERSION="0.1"
SHORTNAME=simple-lookup-service
JAR_FILE=${SHORTNAME}-server-${VERSION}.one-jar.jar
USER=lookup

mvn --version > /dev/null

if [ $? -ne 0 ]; then
    echo "Simple Lookup Service(sLS) uses Maven for packaging. Cannot detect maven in classpath. Please install Maven and set the PATH variable correctly."
    exit 1
fi

mvn clean install

if [ $? -ne 0 ]; then
    echo "\n\nInstallation failed. Maven compilation error!"
    exit 1
fi

BASEDIR="/opt/$SHORTNAME"
SOURCEDIR="$SHORTNAME-server"
CONFIGDIR="/etc/opt/$SHORTNAME"
LOGDIR="/var/log/$SHORTNAME"

if [ -d "$BASEDIR" ]; then
    rm -rf $BASEDIR
fi

if [ -d "$CONFIGDIR" ]; then
    rm -rf $CONFIGDIR
fi

if [ -d "$LOGDIR" ]; then
    rm -rf $LOGDIR
fi

mkdir $BASEDIR
mkdir $CONFIGDIR
mkdir $LOGDIR
mkdir $BASEDIR/target
mkdir $BASEDIR/bin
cp -r $SOURCEDIR/bin/ $BASEDIR/bin
cp -r $SOURCEDIR/etc/ $CONFIGDIR
cp $SOURCEDIR/target/$JAR_FILE $BASEDIR/target/


id -u $USER

if [ $? -ne 0 ]; then
        echo "Creating user lookup"
        useradd lookup
else
        echo "user lookup exists"
fi

chown -R $USER:$USER $BASEDIR

if [ $? -eq 0 ]; then
    echo "\n\nInstalled Simple Lookup Service(sLS) successfully!!\nNOTE: sLS requires MongoDB. Please ensure MongoDB is installed and running before starting sLS."
else
    echo "\n\nInstallation failed! Please correct errors and run the install script again as root."
fi

exit 0
