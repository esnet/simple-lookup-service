package net.es.mp.authn;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.apache.log4j.Logger;

public class AuthnSubjectFactory {
    private Logger log = Logger.getLogger(AuthnSubjectFactory.class);
    private List<Authenticator> authenticators;
    
    public AuthnSubjectFactory(){
        authenticators = new ArrayList<Authenticator>();
    }
    
    public void addAuthenticator(String className){
        try{
            authenticators.add((Authenticator)this.getClass().getClassLoader().loadClass(className).newInstance());
            log.debug("Loaded authenticator " + className);
        } catch(Exception e){
            log.error("Unable to load authenticator: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public AuthnSubject create(HttpHeaders headers) throws AuthnNotSupportedException, UnableToAuthenticateException{
        for(Authenticator authenticator : authenticators){
            try {
                AuthnSubject user = authenticator.authenticate(headers);
                if(user != null){
                    return user;
                }
            } catch (AuthnNotSupportedException e) {
                log.debug(authenticator.getClass() + ": " + e.getMessage());
            }
        }
        throw new AuthnNotSupportedException("Unable to find supported authentication headers");
    }
}
