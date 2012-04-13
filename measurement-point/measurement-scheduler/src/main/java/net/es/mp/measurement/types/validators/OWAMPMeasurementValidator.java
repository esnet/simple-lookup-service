package net.es.mp.measurement.types.validators;

import java.util.ArrayList;
import java.util.List;

import net.es.mp.types.parameters.OWAMPParams;
import net.es.mp.types.validators.EndpointValidator;
import net.es.mp.types.validators.EnumStringValidator;
import net.es.mp.types.validators.RangeStringValidator;

public class OWAMPMeasurementValidator extends MeasurementValidator{
    
    public OWAMPMeasurementValidator(){
        super(new OWAMPResultValidator());
        
        //Enumerated Values
        List<String> validTypeVals = new ArrayList<String>();
        validTypeVals.add(OWAMPParams.TYPE_VALUE);
        
        List<String> validIPVersion = new ArrayList<String>();
        validIPVersion.add(OWAMPParams.IPV_V6_ONLY);
        validIPVersion.add(OWAMPParams.IPV_V6_ONLY);
        validIPVersion.add(OWAMPParams.IPV_PREFER_V6);
        
        //Field definitions
        this.addFieldDef(OWAMPParams.SOURCE, String.class, false, false, new EndpointValidator());
        this.addFieldDef(OWAMPParams.SOURCE_HOSTNAME, String.class, false, true, new EndpointValidator());
        this.addFieldDef(OWAMPParams.SOURCE_IP, String.class, false, true, new EndpointValidator());
        this.addFieldDef(OWAMPParams.DESTINATION, String.class, false, false, new EndpointValidator());
        this.addFieldDef(OWAMPParams.DESTINATION_HOSTNAME, String.class, false, true, new EndpointValidator());
        this.addFieldDef(OWAMPParams.DESTINATION_IP, String.class, false, true, new EndpointValidator());
        
        this.addFieldDef(OWAMPParams.CONTROLLER, String.class, false, false, new EndpointValidator());
        this.addFieldDef(OWAMPParams.IP_VERSION, String.class, false, false,
                new EnumStringValidator(validIPVersion));
        this.addFieldDef(OWAMPParams.PACKET_COUNT, Integer.class, false, false);
        this.addFieldDef(OWAMPParams.PACKET_WAIT, Double.class, false, false);
        this.addFieldDef(OWAMPParams.PACKET_TIMEOUT, Double.class, true, true);
        this.addFieldDef(OWAMPParams.PACKET_PADDING, Integer.class, true, true);
        this.addFieldDef(OWAMPParams.DSCP, Integer.class, true, true);
        this.addFieldDef(OWAMPParams.TEST_PORTS, String.class, true, true, new RangeStringValidator());
    }

}
