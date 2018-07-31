package net.es.lookup.protocol.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.es.lookup.common.BulkRenewResponse;
import net.es.lookup.common.Message;
import net.es.lookup.common.ReservedKeys;

public class JsonBulkRenewResponse extends BulkRenewResponse {

  public JsonBulkRenewResponse() {

    super();
  }

  public JsonBulkRenewResponse(Map<String, Object> bulkRenewResponse) {

    super(bulkRenewResponse);
  }


}
