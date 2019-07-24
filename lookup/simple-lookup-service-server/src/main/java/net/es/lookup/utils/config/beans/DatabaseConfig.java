package net.es.lookup.utils.config.beans;

public class DatabaseConfig {
  private String DbUrl;
  private int DbPort;
  private long pruneInterval;
  private long pruneThreshold;

  private String DbName;
  private String collName;
  private String userName;
  private String password;


  public DatabaseConfig(String dbUrl, int dbPort, long pruneInterval, long pruneThreshold,
      String dbName, String collName, String userName, String password) {
    DbUrl = dbUrl;
    DbPort = dbPort;
    this.pruneInterval = pruneInterval;
    this.pruneThreshold = pruneThreshold;
    DbName = dbName;
    this.collName = collName;
    this.userName = userName;
    this.password = password;
  }

  public String getDbName() {
    return DbName;
  }

  public void setDbName(String dbName) {
    DbName = dbName;
  }

  public String getCollName() {
    return collName;
  }

  public void setCollName(String collName) {
    this.collName = collName;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
  public String getDbUrl() {
    return DbUrl;
  }

  public void setDbUrl(String dbUrl) {
    this.DbUrl = dbUrl;
  }

  public int getDbPort() {
    return DbPort;
  }

  public void setDbPort(int dbPort) {
    this.DbPort = dbPort;
  }

  public long getPruneInterval() {
    return pruneInterval;
  }

  public void setPruneInterval(long pruneInterval) {
    this.pruneInterval = pruneInterval;
  }

  public long getPruneThreshold() {
    return pruneThreshold;
  }

  public void setPruneThreshold(long pruneThreshold) {
    this.pruneThreshold = pruneThreshold;
  }

  @Override
  public String toString() {
    return "DatabaseConfig{" +
        "DbUrl='" + DbUrl + '\'' +
        ", DbPort=" + DbPort +
        ", pruneInterval=" + pruneInterval +
        ", pruneThreshold=" + pruneThreshold +
        ", DbName='" + DbName + '\'' +
        ", collName='" + collName + '\'' +
        ", userName='" + userName + '\'' +
        ", password='" + password + '\'' +
        '}';
  }
}
