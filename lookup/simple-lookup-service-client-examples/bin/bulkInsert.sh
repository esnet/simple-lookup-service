#!/bin/sh
# REGISTER record to sLS
curl -v -H "Content-Type: application/json" -X POST -d '{
    "ttl" : "PT10M",
    "records": [
      {"host-net-tcp-autotunemaxbuffer-send":["16215 bytes"],"type":["interface"],"location-longitude":["-113.6"],"host-net-tcp-autotunemaxbuffer-recv":["16777216 bytes"],"group-communities":["ComputeCanada","pS-NPToolkit-3.3.1"],"host-net-tcp-maxbacklog":["30000"],"service-name":["Test1"],"host-os-version":["6.4 (Final)"],"host-os-name":["CentOS"],"location-city":["Edmonton"],"host-net-tcp-congestionalgorithm":["reno"],"host-os-kernel":["Linux 2.6.32-358.18.1.el6.aufs.web100.x86_64"],"location-sitename":["University of Alberta - WestGrid"],"host-hardware-memory":["15901.25 MB"],"host-net-tcp-maxbuffer-send":["33554432 bytes"],"host-net-tcp-maxbuffer-recv":["33554432 bytes"],"location-latitude":["53.5"],"pshost-toolkitversion":["3.3.1"],"host-hardware-processorcore":["8"],"location-country":["CA"]},

      {"host-net-tcp-autotunemaxbuffer-send":["16215 bytes"],"type":["interface"],"location-longitude":["-113.6"],"host-net-tcp-autotunemaxbuffer-recv":["16777216 bytes"],"group-communities":["ComputeCanada","pS-NPToolkit-3.3.1"],"host-net-tcp-maxbacklog":["30000"],"service-name":["Test1"],"host-os-version":["6.4 (Final)"],"host-os-name":["CentOS"],"location-city":["Edmonton"],"host-net-tcp-congestionalgorithm":["reno"],"host-os-kernel":["Linux 2.6.32-358.18.1.el6.aufs.web100.x86_64"],"location-sitename":["University of Alberta - WestGrid"],"host-hardware-memory":["15901.25 MB"],"host-net-tcp-maxbuffer-send":["33554432 bytes"],"host-net-tcp-maxbuffer-recv":["33554432 bytes"],"location-latitude":["53.67"],"pshost-toolkitversion":["3.3.1"],"host-hardware-processorcore":["8"],"location-country":["CA"]}
    ]
}' $*
