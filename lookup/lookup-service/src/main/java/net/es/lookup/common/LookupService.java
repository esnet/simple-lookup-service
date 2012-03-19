/**
* This interface defines the API between the REST API handling and the actual Lookup Service.
 *
**/

package net.es.lookup.common;

import java.util.List;

public interface LookupService {

    public RegisterRequest publishService (RegisterRequest registerRequest);

    public QueryResponse query (QueryRequest queryRequest);

}