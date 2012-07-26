package client;



import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import utils.InputConfigReader;


public class LSPerformance {
	
	private static final String INPUT_FILE_NAME = "config.txt";
	private static final String OUTPUT_FILE_NAME = "output.txt";
	private int BENCHMARKMAX=3;
	private static int RUNMAX=10;
	private static int APIMAX=6;
	private static int UNITMAX=3;
	private static int TOTALRUNMAX=APIMAX*RUNMAX;
//	public String[] Benchmarks = new String [BENCHMARKMAX];
	
//	public char [] outPutUnit = new char [UNITMAX];

	private String urls;
	private String url;
	private String recorduri;
	private String deleteuri;
	private String key;
	
	private String Benchmark;
	private String API;
	private String Outputunit;
	private String run;
	private int [] Runs = new int [RUNMAX];
	private String [] temp;
	private String [] APIS = new String [APIMAX];

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
	
	private int getServiceRuns;
	private int getServiceKeyRuns;
	private int deleteServiceRuns;
	private int renewServiceRuns;
	private int queryServiceRuns;
	private int registerServiceRuns;

	private HashMap<String, Object> renewmap;
	private HashMap<String, Object> regmap;
	private HashMap<String, Object> querymap;

	
	private LSClient client;
	private static int serialNo=0;
//	private String API;
	private int numOfMessageSent;
	private double meantime;
	private static Object [][] outPut=new Object[TOTALRUNMAX][6];
	public static Random rand=new Random(); 


	
	public LSPerformance(){
		InputConfigReader icfg = InputConfigReader.getInstance();
		this.urls = icfg.getUrls();
		
		this.url = icfg.getUrl();
		this.recorduri = icfg.getRecorduri();
		this.deleteuri = icfg.getDeleteuri();
		this.key = icfg.getKey();
		System.out.println("key:"+this.key);
		System.out.println("url:"+this.url);
		System.out.println("urls:"+this.urls);


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
		temp=run.trim().split(",");
		for(int i=0;i<temp.length;i++){ 
	        Runs[i]=Integer.parseInt(temp[i]); 
	        System.out.println("-----------------"+Runs[i]);
	    } 
		
		this.Outputunit =icfg.getOutputunit();

		this.recordttlrenew = icfg.getRecordttlrenew();
		this.recordtypereg = icfg.getRecordtypereg();
		this.recordservicelocatorreg = icfg.getRecordservicelocatorreg();
		this.recordservicedomainreg = icfg.getRecordservicedomainreg();
		this.recordprivatekeyreg = icfg.getRecordprivatekeyreg();
		

		this.recordservicetypereg = icfg.getRecordservicetypereg();

		this.recordtypequery = icfg.getRecordtypequery();
		this.recordservicelocatorquery = icfg.getRecordservicelocatorquery();
		this.recordservicetypequery = icfg.getRecordservicetypequery();
		this.recordservicedomainquery = icfg.getRecordservicedomainquery();
		this.recordservicedomainoperatorquery = icfg.getRecordservicedomainoperatorquery();
		this.recordoperatorquery = icfg.getRecordoperatorquery();

		this.getServiceRuns = icfg.getGetServiceRuns();
		System.out.println("getserviceruns"+this.getServiceRuns);

	    this.getServiceKeyRuns = icfg.getGetServiceKeyRuns();
	    this.deleteServiceRuns = icfg.getDeleteServiceRuns();
	    this.renewServiceRuns = icfg.getRenewServiceRuns();
	    this.queryServiceRuns = icfg.getQueryServiceRuns();
	    this.registerServiceRuns = icfg.getRegisterServiceRuns();
	    
	    
	    this.client = new LSClient(url,urls);
		outPut [0][0]= "SerialNO";
		outPut [0][1]= "API";
		outPut [0][2]= "Benchmark";
		outPut [0][3]= "MesNo";
		outPut [0][4]= "MeanTime";
		outPut [0][5]= "unit";
		
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
		
		if(!Benchmark.equals("reallife")){
			for(String api: APIS){
				if(api!=null){
					if(api.equals("getService")){
	  //				System.out.println("+++++++"+Benchmark);
						System.out.println("++++"+Runs.length);
						this.getServiceTest(Runs,0,api, Benchmark);
					}
					else if(api.equals("getServiceKey")){
						this.getServiceKeyTest(Runs,0,api, Benchmark);
					}
					else if(api.equals("deleteService"))
						this.deleteServiceTest(Runs,0,api, Benchmark);
					else if(api.equals("renewService"))
						this.renewServiceTest(Runs,0,api, Benchmark);
					else if(api.equals("queryService"))
						this.queryServiceTest(Runs,0, api,Benchmark);
					else if(api.equals("registerService")){
						this.registerServiceTest(Runs,0,api, Benchmark);				
					}
					else
						System.out.println("Invalid API");
				}
			}
		}	
		else{
			this.getServiceTest(null, getServiceRuns, null, Benchmark);
//			System.out.println("in else~~~~~~~~~~~~~~"+getServiceRuns);
			this.getServiceKeyTest(null, getServiceKeyRuns, null, Benchmark);
//			this.deleteServiceTest(null, deleteServiceRuns, null, Benchmark);
			this.renewServiceTest(null, renewServiceRuns, null, Benchmark);
			this.queryServiceTest(null, queryServiceRuns, null, Benchmark);
			this.registerServiceTest(null, registerServiceRuns, null, Benchmark);			
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
			outPut[serialNo][2]=Benchmark;
			outPut[serialNo][3]=numOfMessageSent;
			outPut[serialNo][4]=meantime;
			outPut[serialNo][5]=Outputunit;
	
			System.out.println("the mean response time of "+time.size()+" runs is "+meantime+Outputunit);
			}
		}

		return meantime;
	}

	public double calMeanForParallel(int [] runs,ArrayList<Thread> thrList,String api,Thread t){
		Date timeBegin = new Date();
//		System.out.println("++++"+runs.length);
	
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
			outPut[serialNo][2]=Benchmark;
			outPut[serialNo][3]=runs[i];
			outPut[serialNo][4]=meantime;
			outPut[serialNo][5]=Outputunit;
			}
		}
		
		return meantime;
	}
	
	
	public double calMeanForReallife(int certainruns, ArrayList<Thread> thrList,String api,Thread t){
		serialNo++;
		Date timeBegin = new Date();
//		if(certainruns!=0){
			for(int j = 0; j< certainruns; j++){
				Thread t1 = new Thread(t);
				thrList.add(new Thread (t1));
				thrList.get(j).start();
			}
			for(int j = 0; j< certainruns; j++){
				try {
					thrList.get(j).join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Date timeEnd = new Date();
			System.out.println("certainruns"+certainruns);
			if(certainruns!=0){
			meantime=(timeEnd.getTime()-timeBegin.getTime())/certainruns;
			}
			else
				meantime =0;
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
			outPut[serialNo][2]=Benchmark;
			outPut[serialNo][3]=certainruns;
			outPut[serialNo][4]=meantime;
			outPut[serialNo][5]=Outputunit;
//		}
		return meantime;
	}

	
	public double getServiceTest(int [] runs, int getServiceRuns, String api,String benchmark ){
		System.out.println("getin"+getServiceRuns);

		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,null);

		else if(benchmark.equals("parallel")){
			GetService gs = new GetService( recorduri, Outputunit, client,null);
			Thread t = new Thread(gs);
			meantime=calMeanForParallel(runs,thrList,api,t);
		}
		else if(benchmark.equals("reallife")){
			GetService gs = new GetService(recorduri, Outputunit, client,null);
			Thread t = new Thread(gs);
			meantime=calMeanForReallife(getServiceRuns,thrList,"getService",t);
		}
		return meantime;
	}


	public double getServiceKeyTest(int [] runs,int getServiceKeyRuns, String api,String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api, null);
		else if(benchmark.equals("parallel")){
			GetServiceKey gsk = new GetServiceKey(recorduri, Outputunit, client,null,key);
			Thread t = new Thread(gsk);
			meantime=calMeanForParallel(runs,thrList,api,t);
		}
		else if(benchmark.equals("reallife")){
			GetServiceKey gsk = new GetServiceKey(recorduri, Outputunit, client,null,key);
			Thread t = new Thread(gsk);
			meantime=calMeanForReallife(getServiceKeyRuns,thrList,"getServiceKey",t);
		}

		return meantime;
	}


	public double deleteServiceTest(int [] runs,int deleteRuns, String api, String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,null);
		else if(benchmark.equals("paralell")){
			DeleteService ds = new DeleteService(deleteuri, Outputunit, client);
			Thread t = new Thread(ds);
			meantime=calMeanForParallel(runs,thrList,api,t);
		}
		else if(benchmark.equals("reallife")){
			DeleteService ds = new DeleteService(deleteuri, Outputunit, client);
			Thread t = new Thread(ds);
			meantime=calMeanForReallife(deleteServiceRuns,thrList,"deleteService",t);
		}
		return meantime;
	}


	public double renewServiceTest(int [] runs,int renewServiceRuns, String api, String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,renewmap);
		else if(benchmark.equals("parallel")){
			RenewService rs = new RenewService(recorduri, Outputunit, client, renewmap);
			Thread t = new Thread(rs);
			meantime=calMeanForParallel(runs,thrList,api,t);
		}
		else if(benchmark.equals("reallife")){
			RenewService rs = new RenewService(recorduri, Outputunit, client, renewmap);
			Thread t = new Thread(rs);
			meantime=calMeanForReallife(renewServiceRuns,thrList,"renewService",t);
		}
		return meantime;
	}


	public double queryServiceTest(int [] runs, int queryServiceRuns,String api,String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,querymap);
		else if(benchmark.equals("parallel")){
			QueryService qs = new QueryService(recorduri, Outputunit, client, querymap);
			Thread t = new Thread(qs);
			meantime=calMeanForParallel(runs,thrList,api,t);
		}
		else if(benchmark.equals("reallife")){
			QueryService qs = new QueryService(recorduri, Outputunit, client, querymap);
			Thread t = new Thread(qs);
			meantime=calMeanForReallife(queryServiceRuns,thrList,"queryService",t);
		}
		return meantime;
	}

	public double registerServiceTest(int [] runs,int registerServiceRuns,String api, String benchmark){
		ArrayList<Thread> thrList= new ArrayList();
		if(benchmark.equals("sequencial"))
			meantime= calMeanTime(runs,api,regmap);
		else if(benchmark.equals("parallel")){
			RegisterService res = new RegisterService(recorduri, Outputunit, client, querymap);
			Thread t = new Thread(res);
			meantime=calMeanForParallel(runs,thrList,api,t);
		}
		else if(benchmark.equals("reallife")){
			RegisterService res = new RegisterService( recorduri, Outputunit, client, querymap);
			Thread t = new Thread(res);
			meantime=calMeanForReallife(registerServiceRuns,thrList,"registerService",t);
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
							String.format("%-15s",outPut[i][2])+
							String.format("%-8s",outPut[i][3])+
							String.format("%-25s",outPut[i][4])+
							String.format("%-8s",outPut[i][5])
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
		for(int i=0;i<=serialNo;i++){
			System.out.print(
					String.format("%-10s", outPut[i][0])+
					String.format("%-20s",outPut[i][1])+
					String.format("%-15s",outPut[i][2])+
					String.format("%-8s",outPut[i][3])+
					String.format("%-25s",outPut[i][4])+
					String.format("%-8s",outPut[i][5])
					);
			System.out.println();
		}
		per.write(outPut, OUTPUT_FILE_NAME);
	}

}


