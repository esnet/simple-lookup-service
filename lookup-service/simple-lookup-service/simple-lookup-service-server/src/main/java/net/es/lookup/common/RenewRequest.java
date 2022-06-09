package net.es.lookup.common;

import java.util.Map;

public abstract class RenewRequest extends Message {

  public RenewRequest() {

    super();
  }

  public RenewRequest(Map<String, Object> map) {

    super(map);
  }
}
