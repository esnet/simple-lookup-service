package net.es.mp.authz;

import java.util.Map;

import net.es.mp.types.MPType;

/**
 * Abstract class that implements an Authorizer with MP type and provides a 
 * default init() implementation. Intended for very simple Authorizer 
 * implementations. More complicated implementations shoudl implement
 * the Authorizer interface directly.
 * 
 * @author Andy Lake<andy@es.net>
 *
 */
abstract public class DefaultAuthorizer implements Authorizer<MPType>{
    
    public void init(Map config) {
        //stub, no init to do
        return;
    }
}
