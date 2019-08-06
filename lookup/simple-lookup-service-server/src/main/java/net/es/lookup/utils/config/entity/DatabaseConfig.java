package net.es.lookup.utils.config.entity;

public class DatabaseConfig{

  private String DBUrl;
  private int DBPort;
  private long pruneThreshold;
  private long pruneInterval;

  private String DBName;
  private String DBCollName;
  private String username;
  private String password;

  public String getDBUrl() {
    return DBUrl;
  }

  public void setDBUrl(String DBUrl) {
    this.DBUrl = DBUrl;
  }

  public int getDBPort() {
    return DBPort;
  }

  public void setDBPort(int DBPort) {
    this.DBPort = DBPort;
  }

  public long getPruneThreshold() {
    return pruneThreshold;
  }

  public void setPruneThreshold(long pruneThreshold) {
    this.pruneThreshold = pruneThreshold;
  }

  public long getPruneInterval() {
    return pruneInterval;
  }

  public void setPruneInterval(long pruneInterval) {
    this.pruneInterval = pruneInterval;
  }

  public String getDBName() {
    return DBName;
  }

  public void setDBName(String DBName) {
    this.DBName = DBName;
  }

  public String getDBCollName() {
    return DBCollName;
  }

  public void setDBCollName(String DBCollName) {
    this.DBCollName = DBCollName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
