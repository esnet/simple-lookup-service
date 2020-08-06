FROM centos:centos8

RUN \
    dnf update -y && \
    dnf install -y epel-release && \
    dnf install -y java-1.8.0-openjdk.x86_64

VOLUME /lookup-service

CMD \
    java -jar /lookup-service/simple-lookup-service-server/target/simple-lookup-service-server-3.0-SNAPSHOT.one-jar.jar -c /lookup-service/simple-lookup-service-server/etc/ -l /lookup-service/simple-lookup-service-server/etc/log4j2.properties
