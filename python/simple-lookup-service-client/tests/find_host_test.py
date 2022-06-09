__author__ = 'sowmya'

import unittest
import sls_client.find_host_info
import json

class find_host_test(unittest.TestCase):

    def test_empty_hostname(self):

        hostname=None
        self.assertRaises(Exception, sls_client.find_host_info.get_host_info, hostname)

        hostname=''
        self.assertRaises(Exception, sls_client.find_host_info.get_host_info, hostname)


    def test_get_ma(self):
         hostname='nettest.lbl.gov'
         result = sls_client.find_host_info.get_host_info(hostname)

         self.assertIsNot(result, None)
         self.assertIsInstance(result,list)

         print result


    def test_get_ma_json(self):
         hostname='hous-pt1.es.net'
         result = sls_client.find_host_info.get_host_info_json(hostname)

         self.assertIsNot(result, None)

         print type(result)
         object = json.loads(result)
         self.assertIsInstance(result,str)
         self.assertIsInstance(object,dict)

         print result