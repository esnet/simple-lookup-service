package net.es.mp.authz;

import net.es.mp.authn.AuthnSubject;
import net.es.mp.types.MPType;

public class AllowAllAuthorizer extends DefaultAuthorizer{

    public AuthzConditions authorize(AuthnSubject subject, String action,
            MPType resource) throws AuthorizationException {
        return new AuthzConditions();
    }
}
