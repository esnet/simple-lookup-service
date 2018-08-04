package net.es.lookup.testsuites;

import net.es.lookup.api.BulkRenewServiceTest;
import net.es.lookup.protocol.json.JsonBulkRenewRequestTest;
import net.es.lookup.protocol.json.JsonBulkRenewResponse;
import net.es.lookup.protocol.json.JsonBulkRenewResponseTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  JsonBulkRenewRequestTest.class,
  JsonBulkRenewResponseTest.class,
  BulkRenewServiceTest.class
})
public class BulkRenewSuite {}
