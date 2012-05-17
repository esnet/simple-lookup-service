package net.es.mp.authn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuthnSubject {
    String name;
    String type;
    Map<String, List<String>> attributes;
    
    public AuthnSubject(String name, String type){
        this.name = name;
        this.type = type;
        this.attributes = new HashMap<String,List<String>>();
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
    public Map<String, List<String>> getAttributes() {
        return this.attributes;
    }

    /**
     * @param attributes the attributes to set
     */
    public void setAttributes(Map<String, List<String>> attributes) {
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
    
    public void addAttribute(String name, String value){
        if(!this.attributes.containsKey(name) || this.attributes.get(name) == null){
            this.attributes.put(name, new ArrayList<String>());
        }
        this.attributes.get(name).add(value);
    }
    
    public List<String> getAttribute(String name){
        if(!this.attributes.containsKey(name)){
            return null;
        }
        return this.attributes.get(name);
    }
}
