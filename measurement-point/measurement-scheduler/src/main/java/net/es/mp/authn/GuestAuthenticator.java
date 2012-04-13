package net.es.mp.authn;

import javax.ws.rs.core.HttpHeaders;

public class GuestAuthenticator implements Authenticator{
    final static public String SUBJECT_TYPE = "GUEST";
    final static public String SUBJECT_NAME = "GUEST";
    
    public AuthnSubject authenticate(HttpHeaders headers)
            throws AuthnNotSupportedException, UnableToAuthenticateException {
        return new AuthnSubject(SUBJECT_NAME, SUBJECT_TYPE);
    }

}
