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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.InputConfigReader;


public class LSPerformance {
	public GetService gs;
	public String urls = "http://localhost:8080/lookup/services";
	public String  url= "http://localhost:8080/lookup/service/";
	public String  recorduri= "e0879a5b-54dd-469c-8f7d-6e50ed896449";
	public String deleteuri = "0c28d22d-8ff4-4efc-a7bd-dea21930357f";
	public String  key = "record-service-domain";
	
	public String Benchmark= "sequencial";
	public String API = "getService";
	public String Outputunit = "s";
	public String run;
	public int [] Runs = new int [RUNMAX];
	public String [] temp;
	public String [] APIS = new String [APIMAX];

	public String recordttlrenew = "PT2H5M2S";
	public String recordtypereg ="service";
	public String recordservicelocatorreg= "http://localhost/accesspoint";
	public String recordservicetypereg= "owamp";
	public String recordservicedomainreg= "es.net";
	public String recordprivatekeyreg= "privatekey1";
	
	public String recordtypequery= "service";
	public String recordservicelocatorquery= "tcp://nash-pt1.es.net:4823";
	public String recordservicetypequery= "ping";
	public String recordservicedomainquery= "es.net,L*";
	public String recordservicedomainoperatorquery= "any";
	public String recordoperatorquery = "all";

	public HashMap<String, Object> renewmap;
	public HashMap<String, Object> regmap;
	public HashMap<String, Object> querymap;

	public static final String INPUT_FILE_NAME = "config.txt";
	public static final String OUTPUT_FILE_NAME = "output.txt";
	public int BENCHMARKMAX=3;
	public static int RUNMAX=10;
	public static int APIMAX=6;
	public static int UNITMAX=3;
	public static int TOTALRUNMAX=APIMAX*RUNMAX;
	public String[] Benchmarks = new String [BENCHMARKMAX];
	
	public char [] outPutUnit = new char [UNITMAX];
	public LSClient client;
	public static int serialNo=0;
//	private String API;
	public int numOfMessageSent;
	public double meantime;
	public static Object [][] outPut=new Object[TOTALRUNMAX][5];
	public static Random rand=new Random(); 


	
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
		if(API.contains(",")){
			APIS=API.trim().split(","); 
			for(int i=0;i<APIS.length;i++){
				System.out.println(APIS[i]);
			}
		}
		else APIS[0]=API;
		for(int i=0;i<APIS.length;i++){
			System.out.println(APIS[i]);
		}
		System.out.println("!!!!!!!"+APIS[0]);
		this.run =icfg.getRuns();
//		System.out.println("-----------------"+this.run);
		temp=run.trim().split(",");
		for(int i=0;i<temp.length;i++){ 
	        Runs[i]=Integer.parseInt(temp[i]); 
	        System.out.println("-----------------"+Runs[i]);
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
			if(api!=null){
				if(api.equals("getService")){
//				System.out.println("+++++++"+Benchmark);
					System.out.println("++++"+Runs.length);
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

	public double calMeanForParallel(int [] runs,ArrayList<Thread> thrList,String api,Thread t){
		Date timeBegin = new Date();
		System.out.println("++++"+runs.length);
		for(int i = 0;i<runs.length;i++){
			if(runs[i]!=0){
			thrList.clear();
			serialNo++;
			for(int j = 0; j< runs[i]; j++){
				Thread t1 = new Thread(t);
				thrList.add(new Thread (t1));
				thrList.get(j).start();
			}

			for(int j = 0; j< runs[i]; j++){
				try {
					thrList.get(j).join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			Date timeEnd = new Date();
			System.out.println("runs[i]"+runs[i]);
			meantime=(timeEnd.getTime()-timeBegin.getTime())/runs[i];
			if(Outputunit.equals("s"))
				meantime = meantime/1000;
			else if(Outputunit.equals("m"))
				meantime = meantime/1000/60;
			else if(Outputunit.equals("h"))
				meantime = meantime/1000/60/60;
			else 
				System.out.println("Invalid outPutUnit.");

			outPut[serialNo][0]=serialNo;
			outPut[serialNo][1]=api;
			outPut[serialNo][2]=runs[i];
			outPut[serialNo][3]=meantime;
			outPut[serialNo][4]=Outputunit;
			}
		}
		return meantime;
	}
	public double getServiceTest(int [] runs, String api,String benchmark ){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,null);

		else if(benchmark.equals("parallel")){
			GetService gs = new GetService(url,urls, recorduri, Outputunit, api, client,null);
			Thread t = new Thread(gs);
			meantime=calMeanForParallel(runs,thrList,api,t);
		}
		else{

		}
		return meantime;
	}


	public double getServiceKeyTest(int [] runs, String api,String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api, null);
			else if(benchmark.equals("parallel")){
				GetServiceKey gsk = new GetServiceKey(url,urls, recorduri, Outputunit, api, client,null,key);
				Thread t = new Thread(gsk);
				meantime=calMeanForParallel(runs,thrList,api,t);
			}
			else{
				
			}
		
		return meantime;
	}


	public double deleteServiceTest(int [] runs,String api, String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,null);
			else if(benchmark.equals("paralell")){
				DeleteService ds = new DeleteService(url,urls, recorduri, Outputunit, api, client);
				Thread t = new Thread(gs);
				meantime=calMeanForParallel(runs,thrList,api,t);
			}
			else{
				
			}
		return meantime;
	}


	public double renewServiceTest(int [] runs,String api, String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,renewmap);
			else if(benchmark.equals("parallel")){
				RenewService rs = new RenewService(url,urls, recorduri, Outputunit, api, client, renewmap);
				Thread t = new Thread(rs);
				meantime=calMeanForParallel(runs,thrList,api,t);
			}
			else{
				
			}
		return meantime;
	}


	public double queryServiceTest(int [] runs, String api,String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,querymap);
			else if(benchmark.equals("parallel")){
				QueryService qs = new QueryService(url,urls, recorduri, Outputunit, api, client, querymap);
				Thread t = new Thread(qs);
				meantime=calMeanForParallel(runs,thrList,api,t);
			}
			else{
				
			}
		return meantime;
	}

	public double registerServiceTest(int [] runs,String api, String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,regmap);
			else if(benchmark.equals("parallel")){
				RegisterService res = new RegisterService(url,urls, recorduri, Outputunit, api, client, querymap);
				Thread t = new Thread(res);
				meantime=calMeanForParallel(runs,thrList,api,t);
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





