package net.es.lookup.common;

import java.util.Map;

public abstract class BulkRenewRequest extends Message {
  public BulkRenewRequest() {
    super();
  }

  public BulkRenewRequest(Map<String, Object> map) {
    super(map);
  }
}
