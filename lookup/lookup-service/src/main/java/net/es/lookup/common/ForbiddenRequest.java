package net.es.lookup.common;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class ForbiddenRequestException extends WebApplicationException {
    public ForbiddenRequestException(String message) {
        super(Response.status(Status.FORBIDDEN).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}