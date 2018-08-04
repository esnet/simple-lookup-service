package net.es.lookup.database;

import java.util.Map;
import mockit.Deencapsulation;
import mockit.Invocation;
import mockit.Mock;
import mockit.MockUp;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;
import net.es.lookup.common.exception.internal.DatabaseException;

public class FakeServiceDaoMongo extends MockUp<ServiceDaoMongoDb> {
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
