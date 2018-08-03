package net.es.lookup.api;

import java.util.Map;
import mockit.Deencapsulation;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import mockit.Mocked;
import net.es.lookup.common.LeaseManager;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.api.InternalErrorException;
import net.es.lookup.common.exception.internal.DatabaseException;
import net.es.lookup.database.ServiceDaoMongoDb;
import org.junit.Test;

public class BulkRenewServiceTest {

  public static class FakeServiceDaoMongo extends MockUp<ServiceDaoMongoDb> {
    @Mock
    void $init(Invocation invocation, String dbname, String collname) {
      ServiceDaoMongoDb dbcontext = invocation.getInvokedInstance();
      Deencapsulation.setField(ServiceDaoMongoDb.class, "instance", dbcontext);
    }

    @Mock
    public Message getRecordByURI(String uri) throws DatabaseException {
      Message message = new Message();
      message.add("type", "host");
      message.add("uri", uri);
      return message;
    }

    @Mock
    public Message bulkUpdate(Map<String, Message> records) throws DatabaseException {
      Message message = new Message();
      message.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, 2);
      return message;
    }
  }



  @Test
  public void bulkRenewTest() throws Exception {
    System.out.println("Executing Bulk Renew Base Test");

    new FakeServiceDaoMongo();
    ServiceDaoMongoDb db = new ServiceDaoMongoDb("lookup", "records");

    BulkRenewService bulkRenewService = new BulkRenewService();
    String bulkRenewalRequest =
        "{'record-uris':['lookup/host/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";

    String bulkRenewalResponse = bulkRenewService.bulkRenew(bulkRenewalRequest);
    System.out.println(bulkRenewalResponse);

    assert bulkRenewalResponse.contains("\"total\":\"2\"");
    assert bulkRenewalResponse.contains("\"renewed\":\"2\"");
    assert bulkRenewalResponse.contains("\"failure\":\"0\"");
  }

  @Test(expected = InternalErrorException.class)
  public void bulkRenewDbFailure() {
    System.out.println("Executing Bulk Renew - DB Failure");
    new FakeServiceDaoMongo() {
      @Mock
      public Message bulkUpdate(Map<String, Message> records) throws DatabaseException {
        throw new DatabaseException("DatabaseException: Error renewing services.");
      }
    };

    BulkRenewService bulkRenewService = new BulkRenewService();
    String bulkRenewalRequest =
        "{'record-uris':['lookup/host/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";
    String bulkRenewalResponse = bulkRenewService.bulkRenew(bulkRenewalRequest);
  }




  @Test
  public void bulkRenewExpiredRecords() {
    System.out.println("Executing Bulk Renew - Expired records");
    new FakeServiceDaoMongo() {
      public Message getRecordByURI(String uri) throws DatabaseException {
        Message message = new Message();
        message.add("type", "host");
        message.add("uri", uri);
        message.add(ReservedKeys.RECORD_EXPIRES,"2018-08-01T02:21:26.642Z");
        return message;
      }

      @Mock
      public Message bulkUpdate(Map<String, Message> records) throws DatabaseException {
        Message message = new Message();
        message.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, 0);
        return message;
      }
    };

    BulkRenewService bulkRenewService = new BulkRenewService();
    String bulkRenewalRequest =
        "{'record-uris':['lookup/host/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";
    String bulkRenewalResponse = bulkRenewService.bulkRenew(bulkRenewalRequest);
    System.out.println(bulkRenewalResponse);
    assert bulkRenewalResponse.contains("\"total\":\"2\"");
    assert bulkRenewalResponse.contains("\"renewed\":\"0\"");
    assert bulkRenewalResponse.contains("\"failure\":\"2\"");
  }


  @Test
  public void bulkRenewNotFoundRecords() {
    System.out.println("Executing Bulk Renew - Not found records");
    new FakeServiceDaoMongo() {
      @Mock
      public Message getRecordByURI(String uri) throws DatabaseException {
        return null;
      }

      @Mock
      public Message bulkUpdate(Map<String, Message> records) throws DatabaseException {
        Message message = new Message();
        message.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, 0);
        return message;
      }
    };

    BulkRenewService bulkRenewService = new BulkRenewService();
    String bulkRenewalRequest =
        "{'record-uris':['lookup/host/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj'], 'ttl': 'PT2H'}";
    String bulkRenewalResponse = bulkRenewService.bulkRenew(bulkRenewalRequest);
    System.out.println(bulkRenewalResponse);
    assert bulkRenewalResponse.contains("\"total\":\"2\"");
    assert bulkRenewalResponse.contains("\"renewed\":\"0\"");
    assert bulkRenewalResponse.contains("\"failure\":\"2\"");
  }


  @Test
  public void bulkRenewPartialFailure() {
    System.out.println("Executing Bulk Renew - Partial Failures");
    new FakeServiceDaoMongo() {
      @Mock
      public Message getRecordByURI(String uri) throws DatabaseException {
        if(uri.contains("lookup/host/72384638-b79c-4a51-8f0b-aca9f974203b")){
          Message message = new Message();
          message.add("type", "host");
          message.add("uri", uri);
          message.add(ReservedKeys.RECORD_EXPIRES,"2018-08-01T02:21:26.642Z");
          return message;
        }else if(uri.contains("lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj")){
          Message message = new Message();
          message.add("type", "host");
          message.add("uri", uri);
          return message;
        }
        return null;
      }

      @Mock
      public Message bulkUpdate(Map<String, Message> records) throws DatabaseException {
        Message message = new Message();
        message.add(ReservedKeys.RECORD_BULKRENEW_RENEWEDCOUNT, 1);
        return message;
      }
    };

    BulkRenewService bulkRenewService = new BulkRenewService();
    String bulkRenewalRequest =
        "{'record-uris':['lookup/host/72384638-b79c-4a51-8f0b-aca9f974203b','lookup/host/72384638-b79c-4a51-8f0b-abcd5g8kj', 'lookup/host/1234567-b79c-4a51-8f0b-abcdefgh'], 'ttl': 'PT2H'}";
    String bulkRenewalResponse = bulkRenewService.bulkRenew(bulkRenewalRequest);
    System.out.println(bulkRenewalResponse);
    assert bulkRenewalResponse.contains("\"total\":\"3\"");
    assert bulkRenewalResponse.contains("\"renewed\":\"1\"");
    assert bulkRenewalResponse.contains("\"failure\":\"2\"");
    assert bulkRenewalResponse.contains("\"error-code\":[\"21\",\"22\"]");
  }


}

