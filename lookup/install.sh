#!/usr/bin/bash

APP_NAME="Simple Lookup Service"
VERSION="1.0"
SHORTNAME=lookup-service
JAR_FILE=${SHORTNAME}-server-${VERSION}.one-jar.jar
USER=lookup

mvn --version > /dev/null

if [ $? -ne 0 ]; then
    echo "Simple Lookup Service(sLS) uses Maven for packaging. Cannot detect maven in classpath. Please install Maven and set the PATH variable correctly."
    exit 1
fi

mvn clean install -DskipTests

if [ $? -ne 0 ]; then
    echo "\n\nInstallation failed. Maven compilation error!"
    exit 1
fi

BASEDIR="/opt/$SHORTNAME"
SOURCEDIR="$SHORTNAME-server"
CONFIGDIR="/etc/opt/$SHORTNAME"
LOGDIR="/var/log/$SHORTNAME"
RUNDIR="/var/run/$SHORTNAME"

if [ -d "$BASEDIR" ]; then
    rm -rf $BASEDIR
fi

if [ -d "$CONFIGDIR" ]; then
    rm -rf $CONFIGDIR
fi

if [ -d "$LOGDIR" ]; then
    rm -rf $LOGDIR
fi

if [ -d "$RUNDIR" ]; then
    rm -rf $RUNDIR
fi

mkdir $BASEDIR
mkdir $CONFIGDIR
mkdir $LOGDIR
mkdir $RUNDIR
mkdir $BASEDIR/target
mkdir $BASEDIR/bin
mkdir $BASEDIR/scripts
mkdir $BASEDIR/data
cp -r $SOURCEDIR/bin/* $BASEDIR/bin/
cp -r $SOURCEDIR/etc/* $CONFIGDIR/
cp $SOURCEDIR/target/$JAR_FILE $BASEDIR/target/

if [ -f /etc/redhat-release ] ; then
    cp $SOURCEDIR/scripts/lookup-service-centos $BASEDIR/scripts/$SHORTNAME
    if [ -f /etc/init.d/$SHORTNAME ] ; then
        rm /etc/init.d/$SHORTNAME
    fi
    ln -s $BASEDIR/scripts/$SHORTNAME /etc/init.d/$SHORTNAME
    /sbin/chkconfig --add $SHORTNAME
elif [ -f /etc/lsb-release ] ; then
    cp $SOURCEDIR/scripts/lookup-service-ubuntu $BASEDIR/scripts/$SHORTNAME
fi

id -u $USER

if [ $? -ne 0 ]; then
        echo "Creating user lookup"
        useradd lookup
else
        echo "user lookup exists"
fi

#set permissions
chown -R $USER:$USER $BASEDIR
chown -R $USER:$USER $CONFIGDIR
chown -R $USER:$USER $RUNDIR
chown -R $USER:$USER $LOGDIR

chmod 755 $BASEDIR/bin/*
chmod 755 $BASEDIR/scripts/*
chmod 766 $BASEDIR/data

if [ $? -eq 0 ]; then
    echo "\n\nInstalled Simple Lookup Service(sLS) successfully!!\nNOTE: sLS requires MongoDB. Please ensure MongoDB is installed and running before starting sLS."
else
    echo "\n\nInstallation failed! Please correct errors and run the install script again as root."
fi

exit 0
