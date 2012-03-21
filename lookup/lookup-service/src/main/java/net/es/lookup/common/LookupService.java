/**
* This interface defines the API between the REST API handling and the actual Lookup Service.
 *
**/

package net.es.lookup.common;

import java.util.List;

public interface LookupService {

    public RegisterResponse publishService (RegisterRequest registerRequest);

    public DeleteResponse deleteService(DeleteRequest deleteRequest);
    
    public RenewResponse renewService(RenewRequest renewRequest);
    
    public Service getServiceByURI (String URI);

    public QueryResponse query (QueryRequest queryRequest);

}