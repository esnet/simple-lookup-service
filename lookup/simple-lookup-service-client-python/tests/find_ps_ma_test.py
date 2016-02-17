__author__ = 'sowmya'

import unittest
import sls_client.find_ma
import json

class find_ps_ma_test(unittest.TestCase):

    def test_empty_hostname(self):

        hostname=None
        self.assertRaises(Exception, sls_client.find_ma.get_ma_for_host, hostname)

        hostname=''
        self.assertRaises(Exception, sls_client.find_ma.get_ma_for_host, hostname)


    def test_get_ma(self):
        hostname='hous-pt1.es.net'
        result = sls_client.find_ma.get_ma_for_host(hostname)

        self.assertIsNot(result, None)
        self.assertIsInstance(result,list)

        print result


    def test_get_ma_json(self):
        hostname='hous-pt1.es.net'
        result = sls_client.find_ma.get_ma_for_host_json(hostname)

        self.assertIsNot(result, None)

        print type(result)
        object = json.loads(result)
        self.assertIsInstance(result,str)
        self.assertIsInstance(object,dict)

        print result