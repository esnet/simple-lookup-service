package net.es.lookup.utils.config.entity;

public class LookupServiceConfig extends ConfigEntity{

  private WebServiceConfig webservice;
  private DatabaseConfig database;

  public LookupServiceConfig() {
  }

  public WebServiceConfig getWebservice() {
    return webservice;
  }

  public void setWebservice(WebServiceConfig webservice) {
    this.webservice = webservice;
  }

  public DatabaseConfig getDatabase() {
    return database;
  }

  public void setDatabase(DatabaseConfig database) {
    this.database = database;
  }
}
