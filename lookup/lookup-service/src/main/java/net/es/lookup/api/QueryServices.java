package net.es.lookup.api;

import java.util.List;


import net.es.lookup.common.Message;
import net.es.lookup.common.Service;
import net.es.lookup.database.ServiceDAOMongoDb;
import net.es.lookup.protocol.json.JSONMessage;

public class QueryServices {


    private String params;

    public String query(Message request, int maxResult, int skip) {

        String response;

        // Query DB
        List<Service> res = ServiceDAOMongoDb.getInstance().query(request, maxResult, skip);

        // Build response
        response = JSONMessage.toString(res);
        return response;
    }

}
