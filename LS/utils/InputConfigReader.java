package utils;

import java.util.Map;
import java.util.HashMap;




public class InputConfigReader {
    private static InputConfigReader instance;
    private static final String DEFAULT_FILE = "input.yaml";
    private static final String DEFAULT_PATH = "config";
    Map<String,String> inputMap = new HashMap<String,String>();
    private String urls;
    private String  url;
    private String  recorduri;
    private String deleteuri;
    private String  key;
    private String recordttlrenew;
    private String recordtypereg;
    private String recordservicelocatorreg;
    private String recordservicetypereg;
    private String recordservicedomainreg;
    private String recordprivatekeyreg;
    private String recordtypequery;
    private String recordservicelocatorquery;
    private String recordservicetypequery;
    private String recordservicedomainquery;
    private String recordservicedomainoperatorquery;
    private String recordoperatorquery;
    private String Benchmark;
	private String API;
    private String Outputunit;
    private String runs;
    private int getServiceRuns;
    private int getServiceKeyRuns;
    private int deleteServiceRuns;
    private int renewServiceRuns;
    private int queryServiceRuns;
    private int registerServiceRuns;
    
    
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


	public int getGetServiceRuns() {
		return getServiceRuns;
	}

	public int getGetServiceKeyRuns() {
		return getServiceKeyRuns;
	}

	public int getDeleteServiceRuns() {
		return deleteServiceRuns;
	}

	public int getRenewServiceRuns() {
		return renewServiceRuns;
	}

	public int getQueryServiceRuns() {
		return queryServiceRuns;
	}

	public int getRegisterServiceRuns() {
		return registerServiceRuns;
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
        this.recordttlrenew = (String)this.inputMap.get("record-ttl-renew");
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
        
        this.getServiceRuns = Integer.parseInt((String)this.inputMap.get("getServiceRuns"));
        this.getServiceKeyRuns = Integer.parseInt((String)this.inputMap.get("getServiceKeyRuns"));
        this.deleteServiceRuns = Integer.parseInt((String)this.inputMap.get("deleteServiceRuns"));
        this.renewServiceRuns = Integer.parseInt((String)this.inputMap.get("renewServiceRuns"));
        this.queryServiceRuns = Integer.parseInt((String)this.inputMap.get("queryServiceRuns"));
        this.registerServiceRuns = Integer.parseInt((String)this.inputMap.get("registerServiceRuns"));

    }
}
