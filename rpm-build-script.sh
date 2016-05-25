#!/usr/bin
PROJECT_HOME=/home/sowmya/simple-lookup-service
RELEASE=2.1
VERSION=2
RPM_HOME=/home/sowmya/rpmbuild

rm $PROJECT_HOME/*.tar.gz
rm -rf $PROJECT_HOME/simple-lookup-service/
cp -r $PROJECT_HOME/lookup/ $PROJECT_HOME/simple-lookup-service/
tar -cvzf simple-lookup-service-$RELEASE.tar.gz simple-lookup-service/ --exclude='.git'


cp $PROJECT_HOME/simple-lookup-service-$RELEASE.tar.gz $RPM_HOME/SOURCES/
cp $PROJECT_HOME/simple-lookup-service/simple-lookup-service-server/lookup-service.spec $RPM_HOME/SPECS/
#cp $PROJECT_HOME/simple-lookup-service/simple-lookup-service-subscriber/lookup-service-subscriber.spec $RPM_HOME/SPECS/
rpmbuild -ba $RPM_HOME/SPECS/lookup-service.spec
#rpmbuild -ba $RPM_HOME/SPECS/lookup-service-subscriber.spec

rpm --addsign $RPM_HOME/RPMS/noarch/lookup-service-$RELEASE-$VERSION.noarch.rpm
#rpm --addsign $RPM_HOME/RPMS/noarch/lookup-service-subscriber-$RELEASE-$VERSION.noarch.rpm

