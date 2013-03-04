package net.es.lookup.common.exception.api;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;


/**
 * User: sowmya
 * Date: 12/2/12
 * Time: 12:04 PM
 */
public class BadRequestException extends WebApplicationException{
    public BadRequestException(String message) {
        super(Response.status(Response.Status.BAD_REQUEST).entity(message).type(MediaType.TEXT_PLAIN).build());
    }
}
