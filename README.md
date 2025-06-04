# simple-lookup-service
ESnet has designed a REST/JSON based lookup service called the "simple Lookup Service", or sLS. Clients can use simple HTTP POST to register list of key-value pairs called "records" and use HTTP GET to search and retrieve existing records from server. The lookup service may be run as a simple stand-alone server or as a distributed service. This service is initially targeted at perfSONAR, but also can be used as a general purpose search mechanism for any key-value pairs.



## Installing snapshots script
cd to snapshots directory

`sh install-snapshots.sh`

This will install the snapshot script and add it to cron job.

### Log file
Log file - /var/log/lookup-service/snapshots


