package net.es.lookup.utils.config.beans;

public class LeaseConfig {

  private int maxLease;
  private int defaultLease;
  private int minLease;

  public LeaseConfig(int maxLease, int defaultLease, int minLease) {
    this.maxLease = maxLease;
    this.defaultLease = defaultLease;
    this.minLease = minLease;
  }

  public int getMaxLease() {
    return maxLease;
  }

  public void setMaxLease(int maxLease) {
    this.maxLease = maxLease;
  }

  public int getDefaultLease() {
    return defaultLease;
  }

  public void setDefaultLease(int defaultLease) {
    this.defaultLease = defaultLease;
  }

  public int getMinLease() {
    return minLease;
  }

  public void setMinLease(int minLease) {
    this.minLease = minLease;
  }

  @Override
  public String toString() {
    return "LeaseConfig{" +
        "maxLease=" + maxLease +
        ", defaultLease=" + defaultLease +
        ", minLease=" + minLease +
        '}';
  }
}
