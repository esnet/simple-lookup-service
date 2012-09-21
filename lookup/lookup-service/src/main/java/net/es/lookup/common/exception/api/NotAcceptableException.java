package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class NotAcceptableException extends WebApplicationException {

    public NotAcceptableException(String message) {

        super(Response.status(Status.NOT_ACCEPTABLE).entity(message).type(MediaType.TEXT_PLAIN).build());

    }

}