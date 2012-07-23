package client;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class DeleteService implements Runnable{

	String url;
	String urls;
	int [] runs;
	String api;

	double ttl;
	public static Random rand=new Random();
	String recorduri;
	LSClient client;
	String Outputunit;


	public DeleteService(String url, String urls,String recorduri, 
			String Outputunit,String api,LSClient client){

		this.url=url;
		this.urls=urls;
		this.api=api;

		this.recorduri=recorduri;
		this.client=client;
		this.Outputunit=Outputunit;

	}
	public void run(){
		this.measureTTL(api);
	}

	public double measureTTL(String api){


		Date timeBegin = new Date();
		client.deleteService(recorduri);
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
