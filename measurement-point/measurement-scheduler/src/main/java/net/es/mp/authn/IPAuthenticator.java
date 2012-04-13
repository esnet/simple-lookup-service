package net.es.mp.authn;

import javax.ws.rs.core.HttpHeaders;

import net.es.mp.server.MPClientAuthProxyFilter;

public class IPAuthenticator implements Authenticator {
    final static public String SUBJECT_TYPE = "IP-Address";
    
    public AuthnSubject authenticate(HttpHeaders headers)
            throws AuthnNotSupportedException, UnableToAuthenticateException {
        //Verify it has a IP header
        if(headers.getRequestHeader(MPClientAuthProxyFilter.X_FORWARDED_FOR) == null ||
                headers.getRequestHeader(MPClientAuthProxyFilter.X_FORWARDED_FOR).size() == 0){
            throw new AuthnNotSupportedException("No X_FORWARDED_FOR header");
        }
        //Set IP as name
        String name = headers.getRequestHeader(MPClientAuthProxyFilter.X_FORWARDED_FOR).get(0);
        System.out.println("AUTHN SUBJECT:");
        System.out.println("NAME:" + name);
        System.out.println("TYPE:" + SUBJECT_TYPE);
        
        return new AuthnSubject(name, SUBJECT_TYPE);
    }

}
