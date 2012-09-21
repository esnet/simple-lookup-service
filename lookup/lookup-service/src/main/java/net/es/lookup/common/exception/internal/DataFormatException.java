package net.es.lookup.common.exception.internal;

//This exception is to be used only for internal data formatting errors. (eg: Data retrieved from the DB).
//Not to be used for data received from client side. For client-side data use the status field for each message class
//to indicate if the message is valid or not.
public class DataFormatException extends Exception{

	public DataFormatException(String message){

		super(message);

	}

}