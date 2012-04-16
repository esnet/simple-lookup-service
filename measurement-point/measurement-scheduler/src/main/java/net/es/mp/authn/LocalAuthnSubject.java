package net.es.mp.authn;

public class LocalAuthnSubject extends AuthnSubject{
    final static public String SUBJECT_TYPE = "LOCAL";
    final static public String SUBJECT_NAME = "LOCAL";
    
    public LocalAuthnSubject() {
        super(SUBJECT_NAME, SUBJECT_TYPE);
    }

}
