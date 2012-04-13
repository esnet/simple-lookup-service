package net.es.mp.types.validators;

import java.util.Map;

import net.es.mp.types.MPType;

public class MPTypeValidator extends BaseTypeValidator{
    protected Map<String, FieldDefinition> fieldDefs;
    protected Map<String, Map<String,TypeConverter>> converters;
    
    public MPTypeValidator() {
        super();
        this.addFieldDef(MPType.TYPE, String.class, false, false);
        this.addFieldDef(MPType.URI, String.class, false, false, new URIValidator());
    }
    
}
