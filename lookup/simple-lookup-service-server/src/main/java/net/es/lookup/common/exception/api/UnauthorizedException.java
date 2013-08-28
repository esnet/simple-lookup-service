package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class UnauthorizedException extends WebApplicationException {
    public UnauthorizedException(String message) {
        super(Response.status(Status.UNAUTHORIZED).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
