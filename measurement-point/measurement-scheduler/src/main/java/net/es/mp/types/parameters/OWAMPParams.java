package net.es.mp.types.parameters;

public class OWAMPParams {
    static final public String TYPE_VALUE = "owamp";
    
    static final public String SOURCE = "source";
    static final public String DESTINATION = "destination";
    
    static final public String CONTROLLER = "controller";
    static final public String IP_VERSION = "ipVersion";
    static final public String PACKET_COUNT = "packetCount";
    static final public String PACKET_WAIT = "packetWait";
    static final public String PACKET_TIMEOUT = "packetTimeout";
    static final public String PACKET_PADDING = "packetPadding";
    static final public String DSCP = "dscp";
    static final public String TEST_PORTS = "testPorts";
    
    //Measurement only
    static final public String SOURCE_HOSTNAME = "sourceHostname";
    static final public String SOURCE_IP = "sourceIP";
    static final public String DESTINATION_HOSTNAME = "destinationHostname";
    static final public String DESTINATION_IP = "destinationIP";

    public static final String IPV_V6_ONLY = "v6-only";
    public static final String IPV_V4_ONLY = "v4-only";
    public static final String IPV_PREFER_V6 = "prefer-v6";
    
    public static final int DEFAULT_PACKET_COUNT = 100;
    public static final double DEFAULT_PACKET_WAIT = .1;
}
