package net.es.lookup.common;

public interface RenewRequest extends Message{
	public String getURI();
	public int getTTL();
}