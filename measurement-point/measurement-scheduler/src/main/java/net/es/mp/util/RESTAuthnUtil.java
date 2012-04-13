package net.es.mp.util;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import net.es.mp.authn.AuthnNotSupportedException;
import net.es.mp.authn.AuthnSubject;
import net.es.mp.authn.AuthnSubjectFactory;
import net.es.mp.authn.UnableToAuthenticateException;

public class RESTAuthnUtil {
    
    static public AuthnSubject extractAuthnSubject(HttpHeaders httpHeaders, 
            AuthnSubjectFactory factory){
        AuthnSubject authnSubject = null;
        try {
            authnSubject = factory.create(httpHeaders);
        } catch (AuthnNotSupportedException e) {
            //no supported headers
            throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity(e.getMessage()).build());
        } catch (UnableToAuthenticateException e) {
            //headers supported, but authn failed
            throw new WebApplicationException(Response.status(Status.UNAUTHORIZED).entity("Authenication headers found," +
                    " but they did not pass authentication: " + e.getMessage()).build());
        }
        return authnSubject;
    }
}
