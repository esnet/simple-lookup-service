package net.es.mp.measurement.types.validators;

import java.util.ArrayList;
import java.util.List;

import net.es.mp.measurement.types.BWCTLMeasurement;
import net.es.mp.types.parameters.BWCTLParams;
import net.es.mp.types.validators.EndpointValidator;
import net.es.mp.types.validators.EnumStringValidator;
import net.es.mp.types.validators.InvalidMPTypeException;

public class BWCTLMeasurementValidator extends MeasurementValidator{
    public BWCTLMeasurementValidator(){
        super(new BWCTLResultValidator());
        List<String> validTypeVals = new ArrayList<String>();
        validTypeVals.add(BWCTLParams.TYPE_VALUE);
        
        List<String> validTools = new ArrayList<String>();
        validTools.add(BWCTLParams.TOOL_IPERF);
        validTools.add(BWCTLParams.TOOL_THRULAY);
        validTools.add(BWCTLParams.TOOL_NUTTCP);
        
        List<String> validIPVersion = new ArrayList<String>();
        validIPVersion.add(BWCTLParams.IPV_V6_ONLY);
        validIPVersion.add(BWCTLParams.IPV_V6_ONLY);
        validIPVersion.add(BWCTLParams.IPV_PREFER_V6);
        
        List<String> validProtos = new ArrayList<String>();
        validProtos.add(BWCTLParams.PROTOCOL_TCP);
        validProtos.add(BWCTLParams.PROTOCOL_UDP);
        
        this.addFieldDef(BWCTLParams.TOOL_TYPE, String.class, false, false,
                new EnumStringValidator(validTools));
        this.addFieldDef(BWCTLParams.SOURCE, String.class, false, false, new EndpointValidator());
        this.addFieldDef(BWCTLParams.SOURCE_HOSTNAME, String.class, false, true, new EndpointValidator());
        this.addFieldDef(BWCTLParams.SOURCE_IP, String.class, false, true, new EndpointValidator());
        this.addFieldDef(BWCTLParams.DESTINATION, String.class, false, false, new EndpointValidator());
        this.addFieldDef(BWCTLParams.DESTINATION_HOSTNAME, String.class, false, true, new EndpointValidator());
        this.addFieldDef(BWCTLParams.DESTINATION_IP, String.class, false, true, new EndpointValidator());
        this.addFieldDef(BWCTLParams.DURATION, Long.class, false, false);
        this.addFieldDef(BWCTLParams.CONTROLLER, String.class, false, false, new EndpointValidator());
        this.addFieldDef(BWCTLParams.IP_VERSION, String.class, false, false,
                new EnumStringValidator(validIPVersion));
        this.addFieldDef(BWCTLParams.PROTOCOL, String.class, false, false,
                new EnumStringValidator(validProtos));
        
        this.addFieldDef(BWCTLParams.REPORT_INTERVAL, Integer.class, true, true);
        this.addFieldDef(BWCTLParams.BUFFER_LENGTH, Integer.class, true, true);
        this.addFieldDef(BWCTLParams.DSCP, Integer.class, true, true);
        this.addFieldDef(BWCTLParams.TOS, Integer.class, true, true);
        this.addFieldDef(BWCTLParams.PARALLEL_CONN, Integer.class, true, true);
        this.addFieldDef(BWCTLParams.TCP_WINDOW_SIZE, Integer.class, true, true);
        this.addFieldDef(BWCTLParams.TCP_DYN_WINDOW_SIZE, Integer.class, true, true);
        this.addFieldDef(BWCTLParams.UDP_BANDWIDTH, Long.class, true, true);
    }
    
    public void validate(Object objParam) throws InvalidMPTypeException{
        super.validate(objParam);
        
        //check protocol-specific parameters
        BWCTLMeasurement bwctlSched = (BWCTLMeasurement) objParam;
        if(!BWCTLParams.PROTOCOL_TCP.equals(bwctlSched.getProtocol())){
            if(bwctlSched.getTCPWindowSize() != null){
                throw new InvalidMPTypeException("Field " + BWCTLParams.TCP_WINDOW_SIZE + 
                        " can only be used when protocol is " + BWCTLParams.PROTOCOL_TCP + 
                        ", but protocol is set to " + bwctlSched.getProtocol() + ".");
            }
            if(bwctlSched.getTCPDynamicWindowSize() != null){
                throw new InvalidMPTypeException("Field " + BWCTLParams.TCP_DYN_WINDOW_SIZE + 
                        " can only be used when protocol is " + BWCTLParams.PROTOCOL_TCP + 
                        ", but protocol is set to " + bwctlSched.getProtocol() + ".");
            }
        }
        
        if(!BWCTLParams.PROTOCOL_UDP.equals(bwctlSched.getProtocol())){
            if(bwctlSched.getUDPBandwidth() != null){
                throw new InvalidMPTypeException("Field " + BWCTLParams.UDP_BANDWIDTH + 
                        " can only be used when protocol is " + BWCTLParams.PROTOCOL_UDP + 
                        ", but protocol is set to " + bwctlSched.getProtocol() + ".");
            }
        }
    }
}
