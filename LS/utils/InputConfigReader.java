package utils;

import java.util.Map;
import java.util.HashMap;




public class InputConfigReader {

    private static InputConfigReader instance;
    private static final String DEFAULT_FILE = "input.yaml";
    private static final String DEFAULT_PATH = "config";

    Map<String,String> inputMap = new HashMap<String,String>();


    private String urls = "http://localhost:8080/lookup/services";
    private String  url= "http://localhost:8080/lookup/service/";
    private String  recorduri= "e0879a5b-54dd-469c-8f7d-6e50ed896449";
    private String deleteuri = "0c28d22d-8ff4-4efc-a7bd-dea21930357f";
    private String  key = "record-service-domain";

    private String recordttlrenew = "PT2H5M2S";
    private String recordtypereg ="service";
    private String recordservicelocatorreg= "http://localhost/accesspoint";
    private String recordservicetypereg= "owamp";
    private String recordservicedomainreg= "es.net";
    private String recordprivatekeyreg= "privatekey1";
    private String recordtypequery= "service";
    private String recordservicelocatorquery= "tcp://nash-pt1.es.net:4823";
    private String recordservicetypequery= "ping";
    private String recordservicedomainquery= "es.net,L*";
    private String recordservicedomainoperatorquery= "any";
    private String recordoperatorquery = "all";
    
    private String Benchmark= "sequencial";
	private String API = "getService,getSeviceKey";
    private String Outputunit = "s";
    private String runs;
    
    
    
    /**
     * Constructor - private because this is a Singleton
     */
    private InputConfigReader() {
        
    }

    public static InputConfigReader getInstance() {
        if (InputConfigReader.instance == null) {
        	InputConfigReader.instance = new InputConfigReader();
        	InputConfigReader.instance.setInfo(DEFAULT_PATH,DEFAULT_FILE);
        }
        return InputConfigReader.instance;
    }

    public String getDeleteuri() {
		return deleteuri;
	}

	public String getRecordprivatekeyreg() {
		return recordprivatekeyreg;
	}

	public String getRecordoperatorquery() {
		return recordoperatorquery;
	}

	public String getBenchmark() {
		return Benchmark;
	}

	public String getAPI() {
		return API;
	}

	public String getOutputunit() {
		return Outputunit;
	}
    
    public String getUrls() {
		return urls;
	}


	public String getUrl() {
		return url;
	}



	public String getRecorduri() {
		return recorduri;
	}


	public String getKey() {
		return key;
	}


	public String getRecordttlrenew() {
		return recordttlrenew;
	}


	public String getRecordtypereg() {
		return recordtypereg;
	}



	public String getRecordservicelocatorreg() {
		return recordservicelocatorreg;
	}



	public String getRecordservicetypereg() {
		return recordservicetypereg;
	}



	public String getRecordservicedomainreg() {
		return recordservicedomainreg;
	}



	public String getRecordtypequery() {
		return recordtypequery;
	}


	public String getRecordservicelocatorquery() {
		return recordservicelocatorquery;
	}


	public String getRecordservicetypequery() {
		return recordservicetypequery;
	}


	public String getRecordservicedomainquery() {
		return recordservicedomainquery;
	}


	public String getRecordservicedomainoperatorquery() {
		return recordservicedomainoperatorquery;
	}


	public String getRuns() {
		return runs;
	}


	private void setInfo(String path, String fname) {
        ConfigHelper cfg = ConfigHelper.getInstance();
        Map yamlMap = cfg.getConfiguration(path,fname);
        assert yamlMap != null:  "Could not load configuration file from " +
            "file: ${basedir}/"+path + fname;
        this.inputMap = (HashMap)yamlMap.get("input");
        
        this.key = (String)this.inputMap.get("key");
        this.urls = (String)this.inputMap.get("urls");
        this.url = (String)this.inputMap.get("url");
        this.recorduri = (String)this.inputMap.get("recorduri");
        this.deleteuri=(String)this.inputMap.get("deleteuri");
        
        this.API=(String)this.inputMap.get("API");
        System.out.println("!!!!!!!!!"+(String)this.inputMap.get("API"));
        this.Benchmark=(String)this.inputMap.get("Benchmark");
        this.Outputunit=(String)this.inputMap.get("Outputunit");
        this.runs=this.inputMap.get("runs");
        
        this.recordservicedomainoperatorquery = (String)this.inputMap.get("record-service-domain-operator-query");
        System.out.println((String)this.inputMap.get("record-service-domain-operator-query"));
        this.recordservicedomainquery = (String)this.inputMap.get("record-service-domain-query");
        this.recordservicelocatorquery = (String)this.inputMap.get("record-service-locator-query");
        this.recordservicetypequery = (String)this.inputMap.get("record-service-type-query");
        this.recordtypequery =(String) this.inputMap.get("record-type-query");
        this.recordoperatorquery = (String) this.inputMap.get("record-operator-query");
        
        this.recordservicedomainreg = (String)this.inputMap.get("record-service-domain-reg");
        this.recordservicelocatorreg = (String)this.inputMap.get("record-service-locator-reg");
        this.recordservicetypereg =(String)this.inputMap.get("record-service-type-reg");
        this.recordtypereg = (String)this.inputMap.get("record-type-reg");
        this.recordprivatekeyreg = (String)this.inputMap.get("record-privatekey-reg");
        
        this.recordttlrenew = (String)this.inputMap.get("record-ttl-renew");
        
     
    }
}
