package net.es.mp.authn;

import javax.ws.rs.core.HttpHeaders;

public interface Authenticator {
    public AuthnSubject authenticate(HttpHeaders headers) 
        throws AuthnNotSupportedException,UnableToAuthenticateException;
}
