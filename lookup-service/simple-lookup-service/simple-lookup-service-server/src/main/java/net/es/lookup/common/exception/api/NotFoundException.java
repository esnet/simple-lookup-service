package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class NotFoundException extends WebApplicationException {
    public NotFoundException(String message) {
        super(Response.status(Status.NOT_FOUND).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
