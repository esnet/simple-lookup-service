package net.es.lookup.utils.config.beans;

public class LookupServiceConfig {

  private WebServiceConfig webService;
  private DatabaseConfig database;

  public LookupServiceConfig(WebServiceConfig webService,
      DatabaseConfig database) {
    this.webService = webService;
    this.database = database;
  }

  public WebServiceConfig getWebService() {
    return webService;
  }

  public void setWebService(WebServiceConfig webService) {
    this.webService = webService;
  }

  public DatabaseConfig getDatabase() {
    return database;
  }

  public void setDatabase(DatabaseConfig database) {
    this.database = database;
  }

  @Override
  public String toString() {
    return "LookupServiceConfig{" +
        "webService=" + webService.toString() +
        ", database=" + database.toString() +
        '}';
  }
}
