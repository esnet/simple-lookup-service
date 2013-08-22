#!/usr/bin/bash

APP_NAME="Simple Lookup Service"
VERSION="0.1"
SHORTNAME=lookup-service
JAR_FILE=${SHORTNAME}-${VERSION}.one-jar.jar

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

BASEDIR="/opt/SimpleLookupService"
SOURCEDIR="lookup-service"

if [ -d "$BASEDIR" ]; then
    rm -rf $BASEDIR
fi

mkdir $BASEDIR
mkdir $BASEDIR/log
mkdir $BASEDIR/out
mkdir $BASEDIR/process
mkdir $BASEDIR/target
mkdir $BASEDIR/etc
mkdir $BASEDIR/bin
cp -r $SOURCEDIR/etc $BASEDIR/etc
cp -r $SOURCEDIR/bin $BASEDIR/bin
cp $SOURCEDIR/target/$JAR_FILE $BASEDIR/target/

if [ $? -eq 0 ]; then
    echo "\n\nInstalled Simple Lookup Service(sLS) successfully!!\nNOTE: sLS requires MongoDB. Please ensure MongoDB is installed and running before starting sLS."
else
    echo "\n\nInstallation failed! Please correct errors and run the install script again as root."
fi

exit 0
