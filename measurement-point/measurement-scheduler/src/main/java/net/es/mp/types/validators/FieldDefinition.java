package net.es.mp.types.validators;

public class FieldDefinition {
    protected String name;
    protected Class<?> type;
    protected boolean undefinedAllowed;
    protected boolean nullAllowed;
    protected TypeValidator validator;
    
    public FieldDefinition(String name, Class<?> type, boolean undefinedAllowed, 
            boolean nullAllowed){
        this(name, type, undefinedAllowed, nullAllowed, null );
    }
    
    public FieldDefinition(String name, Class<?> type, boolean undefinedAllowed, 
            boolean nullAllowed, TypeValidator validator){
        this.name = name;
        this.type = type;
        this.undefinedAllowed = undefinedAllowed;
        this.nullAllowed = nullAllowed;
        this.validator = validator;
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
     * @return the type
     */
    public Class getType() {
        return this.type;
    }
    /**
     * @param type the type to set
     */
    public void setType(Class type) {
        this.type = type;
    }
    /**
     * @return the undefinedAllowed
     */
    public boolean isUndefinedAllowed() {
        return this.undefinedAllowed;
    }
    /**
     * @param undefinedAllowed the undefinedAllowed to set
     */
    public void setUndefinedAllowed(boolean undefinedAllowed) {
        this.undefinedAllowed = undefinedAllowed;
    }
    /**
     * @return the nullAllowed
     */
    public boolean isNullAllowed() {
        return this.nullAllowed;
    }
    /**
     * @param nullAllowed the nullAllowed to set
     */
    public void setNullAllowed(boolean nullAllowed) {
        this.nullAllowed = nullAllowed;
    }

    /**
     * @return the validator
     */
    public TypeValidator getValidator() {
        return this.validator;
    }

    /**
     * @param validator the validator to set
     */
    public void setValidator(TypeValidator validator) {
        this.validator = validator;
    }
}
