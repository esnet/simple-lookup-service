package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

public class InternalErrorException extends WebApplicationException {

    public InternalErrorException(String message) {

        super(Response.status(Status.INTERNAL_SERVER_ERROR).entity(message).type(MediaType.TEXT_PLAIN).build());

    }

}