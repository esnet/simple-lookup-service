package net.es.mp.measurement.types;

import net.es.mp.types.parameters.BWCTLParams;

import com.mongodb.DBObject;

public class BWCTLMeasurement extends Measurement{

    public BWCTLMeasurement(DBObject dbo) {
        super(dbo);
        this.setType(BWCTLParams.TYPE_VALUE);
    }
    
    public void setToolType(String value){
        this.dbObject.put(BWCTLParams.TOOL_TYPE, value);
    }
    
    public String getToolType(){
        return (String) this.getField(BWCTLParams.TOOL_TYPE);
    }
    
    public void setSource(String value){
        this.dbObject.put(BWCTLParams.SOURCE, value);
    }
    
    public String getSource(){
        return (String) this.getField(BWCTLParams.SOURCE);
    }
    
    public void setSourceHostname(String value){
        this.dbObject.put(BWCTLParams.SOURCE_HOSTNAME, value);
    }
    
    public String getSourceHostname(){
        return (String) this.getField(BWCTLParams.SOURCE_HOSTNAME);
    }
    
    public void setSourceIP(String value){
        this.dbObject.put(BWCTLParams.SOURCE_IP, value);
    }
    
    public String getSourceIP(){
        return (String) this.getField(BWCTLParams.SOURCE_IP);
    }
    
    public void setDestination(String value){
        this.dbObject.put(BWCTLParams.DESTINATION, value);
    }
    
    public String getDestination(){
        return (String) this.getField(BWCTLParams.DESTINATION);
    }
    
    public void setDestinationHostname(String value){
        this.dbObject.put(BWCTLParams.DESTINATION_HOSTNAME, value);
    }
    
    public String getDestinationHostname(){
        return (String) this.getField(BWCTLParams.DESTINATION_HOSTNAME);
    }
    
    public void setDestinationIP(String value){
        this.dbObject.put(BWCTLParams.DESTINATION_IP, value);
    }
    
    public String getDestinationIP(){
        return (String) this.getField(BWCTLParams.DESTINATION_IP);
    }
    
    public void setDuration(Long value){
        this.dbObject.put(BWCTLParams.DURATION, value);
    }
    
    public Long getDuration(){
        return (Long) this.getField(BWCTLParams.DURATION);
    }
    
    public void setController(String value){
        this.dbObject.put(BWCTLParams.CONTROLLER, value);
    }
    
    public String getController(){
        return (String) this.getField(BWCTLParams.CONTROLLER);
    }
    
    public void setIPVersion(String value){
        this.dbObject.put(BWCTLParams.IP_VERSION, value);
    }
    
    public String getIPVersion(){
        return (String) this.getField(BWCTLParams.IP_VERSION);
    }
    
    public void setProtocol(String value){
        this.dbObject.put(BWCTLParams.PROTOCOL, value);
    }
    
    public String getProtocol(){
        return (String) this.getField(BWCTLParams.PROTOCOL);
    }
    
    public void setReportInterval(Integer value){
        this.dbObject.put(BWCTLParams.REPORT_INTERVAL, value);
    }
    
    public Integer getReportInterval(){
        return (Integer) this.getField(BWCTLParams.REPORT_INTERVAL);
    }
    
    public void setBufferLength(Integer value){
        this.dbObject.put(BWCTLParams.BUFFER_LENGTH, value);
    }
    
    public Integer getBufferLength(){
        return (Integer) this.getField(BWCTLParams.BUFFER_LENGTH);
    }
    
    public void setDSCP(Integer value){
        this.dbObject.put(BWCTLParams.DSCP, value);
    }
    
    public Integer getDSCP(){
        return (Integer) this.getField(BWCTLParams.DSCP);
    }
    
    public void setTOS(Integer value){
        this.dbObject.put(BWCTLParams.TOS, value);
    }
    
    public Integer getTOS(){
        return (Integer) this.getField(BWCTLParams.TOS);
    }
    
    public void setParallelConnections(Integer value){
        this.dbObject.put(BWCTLParams.PARALLEL_CONN, value);
    }
    
    public Integer getParallelConnections(){
        return (Integer) this.getField(BWCTLParams.PARALLEL_CONN);
    }
    
    public void setTCPWindowSize(Integer value){
        this.dbObject.put(BWCTLParams.TCP_WINDOW_SIZE, value);
    }
    
    public Integer getTCPWindowSize(){
        return (Integer) this.getField(BWCTLParams.TCP_WINDOW_SIZE);
    }
    
    public void setTCPDynamicWindowSize(Integer value){
        this.dbObject.put(BWCTLParams.TCP_DYN_WINDOW_SIZE, value);
    }
    
    public Integer getTCPDynamicWindowSize(){
        return (Integer) this.getField(BWCTLParams.TCP_DYN_WINDOW_SIZE);
    }
    
    public void setUDPBandwidth(Long value){
        this.dbObject.put(BWCTLParams.UDP_BANDWIDTH, value);
    }
    
    public Long getUDPBandwidth(){
        return (Long) this.getField(BWCTLParams.UDP_BANDWIDTH);
    }
}
