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
        response = self.client.post("/lookup/records", '{"client-test-key1":["value1"],"type":["client-test"],"client-test-key2":["value2"],"client-test-key3":["value3","value32"],"client-test-name":["Test1"],"client-test-key4":["value4"],"client-test-value5":["%s"]}'%randNum)

class WebsiteUser(HttpLocust):
    resource.setrlimit(resource.RLIMIT_NOFILE, (10240, 9223372036854775807))
    task_set = UserBehavior
    min_wait = 5000
    max_wait = 15000