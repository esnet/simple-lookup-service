package net.es.mp.authn;

import java.util.HashMap;
import java.util.Map;

public class AuthnSubject {
    String name;
    String type;
    Map<String, String> attributes;
    
    public AuthnSubject(String name, String type){
        this.name = name;
        this.type = type;
        this.attributes = new HashMap<String,String>();
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the attributes
     */
    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
