package net.es.lookup.common;

public interface DeleteResponse extends Message{
	public void setError(int code);
	public void setErrorMessage(String s);
}