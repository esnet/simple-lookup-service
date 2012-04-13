package net.es.mp.scheduler.types;

import net.es.mp.types.parameters.OWAMPParams;

import com.mongodb.DBObject;

public class OWAMPSchedule extends Schedule{

    public OWAMPSchedule(DBObject dbo) {
        super(dbo);
        this.setType(OWAMPParams.TYPE_VALUE);
    }
    
    public void setSource(String value){
        this.dbObject.put(OWAMPParams.SOURCE, value);
    }
    
    public String getSource(){
        return (String) this.getField(OWAMPParams.SOURCE);
    }
    
    public void setDestination(String value){
        this.dbObject.put(OWAMPParams.DESTINATION, value);
    }
    
    public String getDestination(){
        return (String) this.getField(OWAMPParams.DESTINATION);
    }
    
    public void setController(String value){
        this.dbObject.put(OWAMPParams.CONTROLLER, value);
    }
    
    public String getController(){
        return (String) this.getField(OWAMPParams.CONTROLLER);
    }
    
    public void setIPVersion(String value){
        this.dbObject.put(OWAMPParams.IP_VERSION, value);
    }
    
    public String getIPVersion(){
        return (String) this.getField(OWAMPParams.IP_VERSION);
    }
    
    public void setPacketCount(Integer value){
        this.dbObject.put(OWAMPParams.PACKET_COUNT, value);
    }
    
    public Integer getPacketCount(){
        return (Integer) this.getField(OWAMPParams.PACKET_COUNT);
    }
    
    public void setPacketWait(Double value){
        this.dbObject.put(OWAMPParams.PACKET_WAIT, value);
    }
    
    public Double getPacketWait(){
        return (Double) this.getField(OWAMPParams.PACKET_WAIT);
    }
    
    public void setPacketTimeout(Double value){
        this.dbObject.put(OWAMPParams.PACKET_TIMEOUT, value);
    }
    
    public Double getPacketTimeout(){
        return (Double) this.getField(OWAMPParams.PACKET_TIMEOUT);
    }
    
    public void setPacketPadding(Integer value){
        this.dbObject.put(OWAMPParams.PACKET_PADDING, value);
    }
    
    public Integer getPacketPadding(){
        return (Integer) this.getField(OWAMPParams.PACKET_PADDING);
    }
    
    public void setDSCP(Integer value){
        this.dbObject.put(OWAMPParams.DSCP, value);
    }
    
    public Integer getDSCP(){
        return (Integer) this.getField(OWAMPParams.DSCP);
    }
    
    public void setTestPorts(String value){
        this.dbObject.put(OWAMPParams.TEST_PORTS, value);
    }
    
    public String getTestPorts(){
        return (String) this.getField(OWAMPParams.TEST_PORTS);
    }
}
