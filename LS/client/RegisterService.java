package client;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class RegisterService implements Runnable{

	String url;
	String urls;
	int [] runs;
	String api;
	HashMap<String,Object> map;
	double ttl;
	public static Random rand=new Random();
	String recorduri;
	LSClient client;
	String Outputunit;


	public RegisterService(String url, String urls,String recorduri, 
			String Outputunit,String api,LSClient client, HashMap<String,Object> map){

		this.url=url;
		this.urls=urls;
		this.api=api;
		this.map=map;
		this.recorduri=recorduri;
		this.client=client;
		this.Outputunit=Outputunit;

	}
	public void run(){
		this.measureTTL(api,map);
	}

	public double measureTTL(String api,HashMap<String,Object> map){


		Date timeBegin = new Date();
		int rundnum =rand.nextInt(100);
		System.out.println("rundun="+rundnum);
		String locator = (String)map.get("record-service-locator")+rundnum;
		map.put("record-service-locator", locator);
		client.register(map);
		Date timeEnd = new Date();

		ttl = timeEnd.getTime() - timeBegin.getTime();

		if(Outputunit.equals("s"))
			ttl = ttl/1000;
		else if(Outputunit.equals("m"))
			ttl = ttl/1000/60;
		else if(Outputunit.equals("h"))
			ttl = ttl/1000/60/60;
		else 
			System.out.println("Invalid outPutUnit.");

		System.out.println("ttl= "+ttl);

		return ttl;
	}


}
