#!/bin/bash

mkdir -p /usr/lib/lookup-service/snapshots/
mkdir -p /var/log/lookup-service/snapshots/

# Install dependencies
pip3 install -r snapshots/requirements.txt

#Copy snapshot scripts
cp snapshots/snapshots.py /usr/lib/lookup-service/snapshots/snapshots.py

# Add to cron
cp snapshots/ls-daily-snapshots.sh /etc/cron.daily/ls-daily-snapshots.sh
chmod 744 /etc/cron.daily/ls-daily-snapshots.sh



