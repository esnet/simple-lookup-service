package net.es.lookup.nulldb;

import java.util.*;

import net.es.lookup.common.Service;
import net.es.lookup.common.LookupService;
import net.es.lookup.common.QueryRequest;
import net.es.lookup.common.QueryResponse;
import net.es.lookup.common.RegisterRequest;
import net.es.lookup.common.RegisterResponse;
import net.es.lookup.common. KeyValue;

public class NullDBLookupService implements LookupService {

    private static final NullDBLookupService instance = new NullDBLookupService();

    private HashMap<String, Service> services = new HashMap<String, Service>();

    static NullDBLookupService getInstance() {
        return NullDBLookupService.instance;
    }


    public RegisterResponse publishService (RegisterRequest registerRequest) {
        return null;
    }

    public Service getServiceByURI (String URI) {
        return null;
    }

    public QueryResponse query (QueryRequest queryRequest) {
        return null;
    }
    
    public Service deleteService(String URI, List<KeyValue> keyValues){
    	return null;
    }
    
    public Service renewService(String URI, List<KeyValue> keyValues){
    	return null;
    }

    private synchronized boolean serviceExists (Service service) {
        Collection<Service> records = this.services.values();
        for (Service tmp : records) {
            if (tmp.equals(service)) {
                return true;
            }
        }
        return false;
    }

}