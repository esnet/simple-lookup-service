package client;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import utils.InputConfigReader;


public class LSPerformance {
	private String urls = "http://localhost:8080/lookup/services";
	private String  url= "http://localhost:8080/lookup/service/";
	private String  recorduri= "e0879a5b-54dd-469c-8f7d-6e50ed896449";
	private String deleteuri = "0c28d22d-8ff4-4efc-a7bd-dea21930357f";
	private String  key = "record-service-domain";
	
	private String Benchmark= "sequencial";
	private String API = "getService";
	private String Outputunit = "s";
	private String run;
	private int [] Runs = new int [RUNMAX];
	private String [] temp;
	private String [] APIS = new String [APIMAX];

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

	private HashMap<String, Object> renewmap;
	private HashMap<String, Object> regmap;
	private HashMap<String, Object> querymap;

	private static final String INPUT_FILE_NAME = "config.txt";
	private static final String OUTPUT_FILE_NAME = "output.txt";
	private int BENCHMARKMAX=3;
	private static int RUNMAX=10;
	private static int APIMAX=6;
	private static int UNITMAX=3;
	private static int TOTALRUNMAX=APIMAX*RUNMAX;
	private String[] Benchmarks = new String [BENCHMARKMAX];
	
	private char [] outPutUnit = new char [UNITMAX];
	private LSClient client;
	private static int serialNo=0;
//	private String API;
	private int numOfMessageSent;
	private double meantime;
	private static Object [][] outPut=new Object[TOTALRUNMAX][5];
	private static Random rand=new Random(); 


	public LSPerformance(){
		
		this.client = new LSClient(url,urls);
		outPut [0][0]= "SerialNO";
		outPut [0][1]= "API";
		outPut [0][2]= "MesNo";
		outPut [0][3]= "MeanTime";
		outPut [0][4]= "unit";
		
		InputConfigReader icfg = InputConfigReader.getInstance();
		this.urls = icfg.getUrls();
		this.url = icfg.getUrl();
		this.recorduri = icfg.getRecorduri();
		this.deleteuri = icfg.getDeleteuri();
		this.key = icfg.getKey();
		
		this.Benchmark = icfg.getBenchmark();
		this.API = icfg.getAPI();
		APIS=API.trim().split(","); 
		for(int i=0;i<APIS.length;i++){
			System.out.println(APIS[i]);
		}
		this.run =icfg.getRuns();
		System.out.println("-----------------"+this.run);
		temp=run.trim().split(",");
		for(int i=0;i<temp.length;i++){ 
	        Runs[i]=Integer.parseInt(temp[i]); 
	    } 
		
		
//		System.out.println("-----------------"+this.Benchmark);
		this.Outputunit =icfg.getOutputunit();

		this.recordttlrenew = icfg.getRecordttlrenew();
		this.recordtypereg = icfg.getRecordtypereg();
		this.recordservicelocatorreg = icfg.getRecordservicelocatorreg();
//		System.out.println("-----------------"+this.recordservicelocatorreg);
		this.recordservicedomainreg = icfg.getRecordservicedomainreg();
		this.recordprivatekeyreg = icfg.getRecordprivatekeyreg();
		

		this.recordservicetypereg = icfg.getRecordservicetypereg();

		this.recordtypequery = icfg.getRecordtypequery();
		this.recordservicelocatorquery = icfg.getRecordservicelocatorquery();
		this.recordservicetypequery = icfg.getRecordservicetypequery();
		this.recordservicedomainquery = icfg.getRecordservicedomainquery();
		this.recordservicedomainoperatorquery = icfg.getRecordservicedomainoperatorquery();
		this.recordoperatorquery = icfg.getRecordoperatorquery();

		this.init();

	}

	public void init(){
		
		renewmap = new HashMap();
		renewmap.put("record-ttl",this.recordttlrenew);

		regmap = new HashMap();
		regmap.put("record-type",this.recordtypereg);
		regmap.put("record-service-locator",this.recordservicelocatorreg);
		regmap.put("record-service-type",this.recordservicetypereg);
		regmap.put("record-service-domain",this.recordservicedomainreg);
		regmap.put("record-privatekey",this.recordprivatekeyreg);

		querymap = new HashMap();
		querymap.put("record-type",this.recordtypequery);
		querymap.put("record-service-locator",this.recordservicelocatorquery);
		querymap.put("record-service-type",this.recordservicetypequery);
		querymap.put("record-service-domain",this.recordservicedomainquery);
		querymap.put("record-service-domain-operator",this.recordservicedomainoperatorquery);
		querymap.put("record-operator",this.recordoperatorquery);
		

		for(String api: APIS){
			if(api.equals("getService")){
				System.out.println("+++++++"+Benchmark);
				this.getServiceTest(Runs,api, Benchmark);
			}
			else if(api.equals("getServiceKey")){
				this.getServiceKeyTest(Runs,api, Benchmark);
			}
			else if(api.equals("deleteService"))
				this.deleteServiceTest(Runs,api, Benchmark);
			else if(api.equals("renewService"))
				this.renewServiceTest(Runs,api, Benchmark);
			else if(api.equals("queryService"))
				this.queryServiceTest(Runs, api,Benchmark);
			else if(api.equals("registerService")){
				this.registerServiceTest(Runs,api, Benchmark);				
			}
			else
				System.out.println("Invalid API");
		}
	}


	public double calMeanTime(int [] runs,String api,HashMap<String,Object> map){
		ArrayList<Double> time= new ArrayList();
		//		API = getService;
		double temptime = 0;
		double sumtime=0;

		for(int i=0; i< runs.length;i++){
			if(runs[i]!=0){
			time.clear();
			sumtime=0;
			serialNo++;

			for(int j=0; j<runs[i];j++){
				Date timeBegin = new Date();
				if(api.equals("getService")){
					int randnum1=rand.nextInt(10);
					String recuri1=recorduri+"/?nocache"+randnum1;
					System.out.println("recuril"+recuri1);
					client.getService(recuri1);
				}
				else if(api.equals("getServiceKey")){
					int randnum2=rand.nextInt(10);
					String recuri2=recorduri+"/?nocache"+randnum2;
					client.getServiceKey(recuri2, key);
				}
				else if(api.equals("deleteService"))
					client.deleteService(recorduri);
				else if(api.equals("renewService"))
					client.renew(recorduri, map);
				else if(api.equals("queryService"))
					client.query(map);
				else if(api.equals("registerService")){
					int rundnum3 =rand.nextInt(100);
					System.out.println("rundun="+rundnum3);
					String locator = (String)map.get("record-service-locator")+rundnum3;
					map.put("record-service-locator", locator);

					client.register(map);				
				}
				else
					System.out.println("Invalid API");

				Date timeEnd = new Date();
				temptime = timeEnd.getTime() - timeBegin.getTime();
				
				if(Outputunit.equals("s"))
					temptime = temptime/1000;
				else if(Outputunit.equals("m"))
					temptime = temptime/1000/60;
				else if(Outputunit.equals("h"))
					temptime = temptime/1000/60/60;
				else 
					System.out.println("Invalid outPutUnit.");
				
				time.add(temptime);
				System.out.println("the "+j+"th run response time is"+time.get(j));
				sumtime=sumtime + time.get(j);
			}
			System.out.println("sumtime"+sumtime);
			System.out.println("the number of runs is "+ time.size()); 
			numOfMessageSent = time.size();
			meantime=(double)sumtime/time.size();

			outPut[serialNo][0]=serialNo;
			outPut[serialNo][1]=api;
			outPut[serialNo][2]=numOfMessageSent;
			outPut[serialNo][3]=meantime;
			outPut[serialNo][4]=Outputunit;
	
			System.out.println("the mean response time of "+time.size()+" runs is "+meantime+Outputunit);
			}
		}

		return meantime;
	}

	public double getServiceTest(int [] runs, String api,String benchmark ){
		if(benchmark.equals("sequencial"))
		meantime= calMeanTime(runs,api,null);
		else if(benchmark.equals("paralell")){
			
		}
		else{
			
		}
		return meantime;
	}


	public double getServiceKeyTest(int [] runs, String api,String benchmark){
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api, null);
			else if(benchmark.equals("paralell")){
				
			}
			else{
				
			}
		
		return meantime;
	}


	public double deleteServiceTest(int [] runs,String api, String benchmark){
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,null);
			else if(benchmark.equals("paralell")){
				
			}
			else{
				
			}
		return meantime;
	}


	public double renewServiceTest(int [] runs,String api, String benchmark){
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,renewmap);
			else if(benchmark.equals("paralell")){
				
			}
			else{
				
			}
		return meantime;
	}


	public double queryServiceTest(int [] runs, String api,String benchmark){
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,querymap);
			else if(benchmark.equals("paralell")){
				
			}
			else{
				
			}
		return meantime;
	}

	public double registerServiceTest(int [] runs,String api, String benchmark){
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,regmap);
			else if(benchmark.equals("paralell")){
				
			}
			else{
				
			}
		System.out.println("ddddddddd"+regmap.get("record-service-locator"));
		return meantime;
	}


	/**
	   Write the result array to the given file. 
	 */
	void write(Object [][] outPut, String aOutputFileName){
		try{
			PrintWriter out = new PrintWriter(new FileWriter(aOutputFileName)); 
			try {
				for(int i=0;i<=serialNo;i++){
					out.print(
							String.format("%-10s", outPut[i][0])+
							String.format("%-20s",outPut[i][1])+
							String.format("%-8s",outPut[i][2])+
							String.format("%-25s",outPut[i][3])+
							String.format("%-8s",outPut[i][4])
							);
					out.println();
				}
			} 
			finally{
				out.flush(); 
				out.close();
			}
		}
		catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			System.out.println("File not found.");
		} 
		catch (IOException e){
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		LSPerformance per = new LSPerformance();
//		int runs[]= {10,20};
//		int run[]={1};
//		String benchmark="serial";
//		per.getServiceTest(runs, benchmark);
//		per.getServiceKeyTest(runs, benchmark);
////		per.deleteServiceTest(run, benchmark, "deleteService", deleteuri, 's');
//		per.renewServiceTest(runs, benchmark);
//		per.queryServiceTest(runs, benchmark);
//		per.registerServiceTest(run, benchmark);
		for(int i=0;i<=serialNo;i++){
			System.out.print(
					String.format("%-10s", outPut[i][0])+
					String.format("%-20s",outPut[i][1])+
					String.format("%-8s",outPut[i][2])+
					String.format("%-25s",outPut[i][3])+
					String.format("%-8s",outPut[i][4])
					);
			System.out.println();
		}
		per.write(outPut, OUTPUT_FILE_NAME);
	}

}
