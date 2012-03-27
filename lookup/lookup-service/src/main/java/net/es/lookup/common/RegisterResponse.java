package net.es.lookup.common;

public interface RegisterResponse extends Message{
	public int getError();
	public String getErrorMessage();
	public void setError(int code);
	public void setErrorMessage(String s);
}