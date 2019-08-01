package net.es.lookup.utils.config;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import net.es.lookup.utils.config.entity.ConfigEntity;
import net.es.lookup.utils.config.entity.LookupServiceConfig;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import net.es.lookup.service.LookupService;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class LookupServiceConfigParser implements Parser {
  private static Logger LOG = LogManager.getLogger(LookupService.class);

  private String configFile;

  private LookupServiceConfig lookupServiceConfig;

  public LookupServiceConfig getLookupServiceConfig() {
    return lookupServiceConfig;
  }

  public LookupServiceConfigParser(String configFile) {
    this.configFile = configFile;
  }

  public String getConfigFile() {
    return configFile;
  }

  public void setConfigFile(String configFile) {
    this.configFile = configFile;
  }

  @Override
  public void parse() {
    if (configFile.isEmpty()) {
      LOG.error("Config file not specified. Please specify config file and restart process");
      System.exit(-1);
    }

    Yaml yaml = new Yaml(new Constructor(LookupServiceConfig.class));
    try {
      InputStream inputStream = new FileInputStream(configFile);
      lookupServiceConfig = yaml.load(inputStream);

    } catch (FileNotFoundException e) {
      LOG.error("Config file not found. Please specify correct path.");
      System.exit(-1);
    }
  }
}
