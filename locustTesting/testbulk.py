from locust import HttpLocust, TaskSet, task
import random
import resource


class UserBehavior(TaskSet):
    def on_start(self):
        """ on_start is called when a Locust start before any task is scheduled """
        #self.login()

    @task
    def login(self):
        randNum = random.randint(1,100000)
        response = self.client.post("/lookup/records/bulk", '{ "ttl" : "PT10M","records": [{"host-net-tcp-autotunemaxbuffer-send":["16215 bytes"],"type":["interface"],"location-longitude":["-113.6"],"host-net-tcp-autotunemaxbuffer-recv":["16777216 bytes"],"group-communities":["ComputeCanada","pS-NPToolkit-3.3.1"],"host-net-tcp-maxbacklog":["30000"],"service-name":["Test1"],"host-os-version":["6.4 (Final)"],"host-os-name":["CentOS"],"location-city":["Edmonton"],"host-net-tcp-congestionalgorithm":["reno"],"host-os-kernel":["Linux 2.6.32-358.18.1.el6.aufs.web100.x86_64"],"location-sitename":["University of Alberta - WestGrid"],"host-hardware-memory":["15901.25 MB"],"host-net-tcp-maxbuffer-send":["33554432 bytes"],"host-net-tcp-maxbuffer-recv":["33554432 bytes"],"location-latitude":["53.5"],"pshost-toolkitversion":["3.3.1"],"host-hardware-processorcore":["8"],"location-country":["CA"]}, {"host-net-tcp-autotunemaxbuffer-send":["16215 bytes"],"type":["interface"],"location-longitude":["-113.6"],"host-net-tcp-autotunemaxbuffer-recv":["%s bytes"],"group-communities":["ComputeCanada","pS-NPToolkit-3.3.1"],"host-net-tcp-maxbacklog":["30000"],"service-name":["Test1"],"host-os-version":["6.4 (Final)"],"host-os-name":["CentOS"],"location-city":["Edmonton"],"host-net-tcp-congestionalgorithm":["reno"],"host-os-kernel":["Linux 2.6.32-358.18.1.el6.aufs.web100.x86_64"],"location-sitename":["University of Alberta - WestGrid"],"host-hardware-memory":["15901.25 MB"],"host-net-tcp-maxbuffer-send":["%s bytes"],"host-net-tcp-maxbuffer-recv":["33554432 bytes"],"location-latitude":["53.67"],"pshost-toolkitversion":["3.3.1"],"host-hardware-processorcore":["8"],"location-country":["CA"]}]}'%(randNum,randNum))

class WebsiteUser(HttpLocust):
    resource.setrlimit(resource.RLIMIT_NOFILE, (10240, 9223372036854775807))
    task_set = UserBehavior
    min_wait = 5000
    max_wait = 15000