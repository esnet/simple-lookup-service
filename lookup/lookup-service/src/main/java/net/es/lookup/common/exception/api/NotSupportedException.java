package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Author: sowmya
 * Date: 3/27/13
 * Time: 4:28 PM
 */
public class NotSupportedException extends WebApplicationException {

    public NotSupportedException(String message) {

        super(Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
