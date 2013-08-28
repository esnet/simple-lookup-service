package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
/**
 * User: sowmya
 * Date: 12/2/12
 * Time: 12:07 PM
 */
public class ForbiddenRequestException extends WebApplicationException {
    public ForbiddenRequestException(String message) {
        super(Response.status(Status.FORBIDDEN).entity(message).type(MediaType.TEXT_PLAIN).build());
    }

}
