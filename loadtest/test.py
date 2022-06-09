from locust import HttpLocust, TaskSet, task, between
import random
import resource


class ClientTaskSet(TaskSet):
    def on_start(self):
        """ on_start is called when a Locust start before any task is scheduled """
        #self.login()

    @task(2)
    def register(self):
        randNum = random.randint(3001,1000000)
        response = self.client.post("/lookup/records", '{"client-test-key1":["value1"],"type":["client-test"],"client-test-key2":["value2"],"client-test-key3":["value3","value32"],"client-test-name":["Test1"],"client-test-key4":["value4"],"client-test-value5":["%s"]}'%randNum)
    @task(1)
    def query(self):
        response = self.client.get("/lookup/records")

class WebsiteUser(HttpLocust):
    resource.setrlimit(resource.RLIMIT_NOFILE, (10240, 9223372036854775807))
    task_set = ClientTaskSet
    wait_time = between(2, 10)
