package net.es.mp.authz;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.authn.GuestAuthenticator;
import net.es.mp.types.MPType;

public class DenyWriteGuestAuthorizer extends DefaultAuthorizer{

    public AuthzConditions authorize(AuthnSubject subject, String action, MPType resource)
            throws AuthorizationException {
        if(GuestAuthenticator.SUBJECT_TYPE.equals(subject.getType())){
            if(AuthzAction.CREATE.equals(action)){
                throw new AuthorizationException("You must authenticate to create a new resource");
            }else if(AuthzAction.UPDATE.equals(action)){
                throw new AuthorizationException("You must authenticate to update a resource");
            }else if(AuthzAction.DELETE.equals(action)){
                throw new AuthorizationException("You must authenticate to delete a resource");
            }
        }
        return new AuthzConditions();
    }

}
