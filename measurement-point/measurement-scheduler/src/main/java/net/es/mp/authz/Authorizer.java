package net.es.mp.authz;

import java.util.Map;

import net.es.mp.authn.AuthnSubject;

public interface Authorizer<E>{
    
    public void init(Map config);
    
    public AuthzConditions authorize(AuthnSubject subject, String action, E resource) throws AuthorizationException;
    
}
