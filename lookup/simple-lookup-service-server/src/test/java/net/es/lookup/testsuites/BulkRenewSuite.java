package net.es.lookup.testsuites;

import net.es.lookup.protocol.json.JsonBulkRenewRequestTest;
import net.es.lookup.protocol.json.JsonBulkRenewResponseTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  JsonBulkRenewRequestTest.class,
  JsonBulkRenewResponseTest.class
})
public class BulkRenewSuite {}
