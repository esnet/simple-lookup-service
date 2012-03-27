package net.es.lookup.common;

import java.util.ArrayList;
import net.es.lookup.common.*;

public interface RenewResponse extends Message{
	public void setError(int code);
	public void setErrorMessage(String s);
	public void setResult(ArrayList<net.es.lookup.common.Service> s);
}