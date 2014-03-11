package net.es.lookup.common.exception.internal;

//This exception is to be used only for internal elements formatting errors. (eg: Data retrieved from the DB).
//Not to be used for elements received from client side. For client-side elements use the status field for each message class
//to indicate if the message is valid or not.
public class DataFormatException extends Exception{
    public DataFormatException(String message){
        super(message);
    }
}