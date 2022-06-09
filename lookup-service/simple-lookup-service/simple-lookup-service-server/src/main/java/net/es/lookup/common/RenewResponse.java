package net.es.lookup.common;

import java.util.Map;

public abstract class RenewResponse extends Message {

  public RenewResponse() {

    super();
  }

  public RenewResponse(Map<String, Object> map) {

    super(map);
  }
}
