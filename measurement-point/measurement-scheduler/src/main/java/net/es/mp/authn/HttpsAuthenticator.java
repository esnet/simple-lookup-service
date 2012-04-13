package net.es.mp.authn;

import javax.ws.rs.core.HttpHeaders;

import net.es.mp.server.MPClientAuthProxyFilter;

public class HttpsAuthenticator implements Authenticator {
    
    final static public String SUBJECT_TYPE = "X509";
    final static public String ATTR_ISSUE_DN = "issuerDN";
    
    public AuthnSubject authenticate(HttpHeaders headers) throws AuthnNotSupportedException{
        //Verify it has a DN
        if(headers.getRequestHeader(MPClientAuthProxyFilter.SSL_CLIENT_S_DN) == null ||
                headers.getRequestHeader(MPClientAuthProxyFilter.SSL_CLIENT_S_DN).size() == 0){
            throw new AuthnNotSupportedException("No SSL_CLIENT_S_DN header");
        }
        //Set DN as name
        String name = headers.getRequestHeader(MPClientAuthProxyFilter.SSL_CLIENT_S_DN).get(0);
        AuthnSubject subject = new AuthnSubject(name, SUBJECT_TYPE);
        
        //Set issue name
        if(headers.getRequestHeader(MPClientAuthProxyFilter.SSL_CLIENT_I_DN) != null &&
                headers.getRequestHeader(MPClientAuthProxyFilter.SSL_CLIENT_I_DN).size() != 0){
            String issuer = headers.getRequestHeader(MPClientAuthProxyFilter.SSL_CLIENT_I_DN).get(0);
            subject.getAttributes().put(ATTR_ISSUE_DN, issuer);
        }
        
        return subject;
    }

}
