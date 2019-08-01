package net.es.lookup.utils.config.entity;

public class LeaseConfig {

  private long minVal;
  private long maxVal;
  private long defaultVal;

  public long getMinVal() {
    return minVal;
  }

  public void setMinVal(long minVal) {
    this.minVal = minVal;
  }

  public long getMaxVal() {
    return maxVal;
  }

  public void setMaxVal(long maxVal) {
    this.maxVal = maxVal;
  }

  public long getDefaultVal() {
    return defaultVal;
  }

  public void setDefaultVal(long defaultVal) {
    this.defaultVal = defaultVal;
  }
}
