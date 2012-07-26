package client;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

public class GetServiceKey implements Runnable{
	private HashMap<String,Object> map;
	private double ttl;
	private static Random rand=new Random();
	private String recorduri;
	private LSClient client;
	private String Outputunit;
	private String key;




	public GetServiceKey(String recorduri, String Outputunit,LSClient client, HashMap<String,Object> map,String key){
		this.map=map;
		this.recorduri=recorduri;
		this.client=client;
		this.Outputunit=Outputunit;
		this.key=key;
	}
	
	public void run(){
		this.measureTTL(null);
	}

	public double measureTTL(HashMap<String,Object> map){
		Date timeBegin = new Date();
		int randnum1=rand.nextInt(10);
		String recuri1=recorduri+"/?nocache"+randnum1;
		client.getServiceKey(recuri1,key);
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
