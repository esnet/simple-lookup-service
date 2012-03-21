/**
* This interface defines the API between the REST API handling and the actual Lookup Service.
 *
**/

package net.es.lookup.common;

import java.util.List;

public interface LookupService {

    public RegisterResponse publishService (RegisterRequest registerRequest);

    public Service deleteService(String URI, List<KeyValue> keyValues);
    
    public Service renewService(String URI, List<KeyValue> keyValues);
    
    public Service getServiceByURI (String URI);

    public QueryResponse query (QueryRequest queryRequest);

}