package net.es.lookup.common;

import java.util.*;
import net.es.lookup.common.*;

public interface QueryResponse extends Message{
	public void setError(int code);
	public void setErrorMessage(String s);
	public void setResult(ArrayList<Service> result);
}