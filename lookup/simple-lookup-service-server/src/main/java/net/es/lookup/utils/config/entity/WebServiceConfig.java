package net.es.lookup.utils.config.entity;

public class WebServiceConfig {

  private String host;
  private int port;

  private LeaseConfig lease;

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public LeaseConfig getLease() {
    return lease;
  }

  public void setLease(LeaseConfig lease) {
    this.lease = lease;
  }
}
