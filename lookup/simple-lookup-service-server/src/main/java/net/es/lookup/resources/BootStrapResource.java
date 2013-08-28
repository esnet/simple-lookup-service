package net.es.lookup.resources;

import net.es.lookup.api.AccessService;
import net.es.lookup.api.BootStrapService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * Author: sowmya
 * Date: 8/5/13
 * Time: 4:04 PM
 */
@Path("/lookup/bootstrap")
public class BootStrapResource {

    private BootStrapService bootStrapService = new BootStrapService();

    @GET
    @Produces("application/json")
    public String getHandler() {

        return this.bootStrapService.getServers();

    }

}
