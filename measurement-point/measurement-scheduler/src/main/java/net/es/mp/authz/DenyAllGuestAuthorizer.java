package net.es.mp.authz;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authn.GuestAuthenticator;
import net.es.mp.types.MPType;

public class DenyAllGuestAuthorizer extends DefaultAuthorizer{

    public AuthzConditions authorize(AuthnSubject subject, String action, MPType resource)
            throws AuthorizationException {
        if(GuestAuthenticator.SUBJECT_TYPE.equals(subject.getType())){
            throw new AuthorizationException("You must authenticate to perform action");
        }
        return new AuthzConditions(); 
    }

}
