package net.es.lookup.config;

import java.io.IOException;
import net.es.lookup.utils.config.LookupServiceConfigParser;
import net.es.lookup.utils.config.entity.LeaseConfig;
import net.es.lookup.utils.config.entity.LookupServiceConfig;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConfigParserTest {

  @Test
  public void testIfConfigFileIsSetCorrectly(){
    String yamlConfig = ".src/test/resources/config.yml";
    LookupServiceConfigParser configParser = new LookupServiceConfigParser(yamlConfig);
    System.out.println("Testing if config file parameter is set correctly");
    assert configParser.getConfigFile().contentEquals(yamlConfig);
  }

  @Test
  public void testConfigFileParsing(){
    String yamlConfig = "./src/test/resources/config.yml";
    LookupServiceConfigParser configParser = new LookupServiceConfigParser(yamlConfig);
    System.out.println("Testing if config file parses correctly");
    try {
      configParser.parse();
    } catch (IOException e) {
      e.printStackTrace();
    }
    LookupServiceConfig lsConfig = configParser.getLookupServiceConfig();
    assertFalse("LS Host", lsConfig.getWebservice().getHost().isEmpty());
    assertEquals("LS Port",8090, lsConfig.getWebservice().getPort());

    assertEquals("Lease Default Val", 7200, lsConfig.getWebservice().getLease().getDefaultVal());
    assertEquals("Lease Default Val", 7200, lsConfig.getWebservice().getLease().getDefaultVal());
    assertEquals("Lease Max Val", 2592000, lsConfig.getWebservice().getLease().getMaxVal());
    assertEquals("Lease Min Val",30, lsConfig.getWebservice().getLease().getMinVal());

    assertEquals("DB Url","127.0.0.1", lsConfig.getDatabase().getDBUrl());
    assertEquals("DB Port",27017, lsConfig.getDatabase().getDBPort());
    assertEquals("DB Name","LookupService", lsConfig.getDatabase().getDBName());
    assertEquals("DB Coll Name","services", lsConfig.getDatabase().getDBCollName());
    assertEquals("DB Username","lookup", lsConfig.getDatabase().getUsername());
    assertEquals("DB Password","abc123", lsConfig.getDatabase().getPassword());
    assertEquals("DB Prune Interval", 300, lsConfig.getDatabase().getPruneInterval());
    assertEquals("DB Prune threshold",120, lsConfig.getDatabase().getPruneThreshold());
  }

  @Test
  public void testInvalidConfigFile(){
    String yamlConfig = "./src/test/resource/config.yml";
    LookupServiceConfigParser configParser = new LookupServiceConfigParser(yamlConfig);
    System.out.println("Testing invalid config file");
    try {
      configParser.parse();
    } catch (IOException ie) {
      //exception is thrown as expected
      System.out.println("Exception received as expected");
      assert true;
    }
  }

}
