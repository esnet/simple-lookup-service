package net.es.lookup.resources;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;

import net.es.lookup.service.KeyValue;
import net.sf.json.util.JSONStringer;
import net.sf.json.util.JSONTokener;
import net.sf.json.JSONObject;

/**
 *
 */
@Path("/lookup/service")
public class RegistrationResource {

    // The Java method will process HTTP GET requests
    @POST
    // The Java method will produce content identified by the MIME Media
    // type "text/plain"
    @Produces("text/plain")
    public String registerService() {
        // Return some cliched textual content

        String result = new JSONStringer().object().key("key").value("value1").endObject().toString();
        new JSONTokener(result);
        JSONObject input = JSONObject.fromObject(result);
        KeyValue keyValue = (KeyValue) JSONObject.toBean(input, KeyValue.class);
        return "\n" + keyValue.getKey() + " ### " + keyValue.getValue() + "\n\n";
    }
}

