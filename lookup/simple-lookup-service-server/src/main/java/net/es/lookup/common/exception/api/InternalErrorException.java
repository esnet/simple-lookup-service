package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

/**
 * User: sowmya
 * Date: 12/2/12
 * Time: 12:11 PM
 */
public class InternalErrorException extends WebApplicationException {
    public InternalErrorException(String message) {
        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
