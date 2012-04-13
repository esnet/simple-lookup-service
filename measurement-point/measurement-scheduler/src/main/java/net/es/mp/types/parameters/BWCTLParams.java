package net.es.mp.types.parameters;

public class BWCTLParams {
    static final public String TYPE_VALUE = "bwctl";
    
    static final public String TOOL_TYPE = "toolType";
    static final public String SOURCE = "source";
    static final public String DESTINATION = "destination";
    static final public String DURATION = "duration";
    
    static final public String CONTROLLER = "controller";
    static final public String IP_VERSION = "ipVersion";
    static final public String PROTOCOL = "protocol";
    static final public String REPORT_INTERVAL = "reportInterval";
    static final public String BUFFER_LENGTH = "bufferLength";
    static final public String DSCP = "dscp";
    static final public String TOS = "tos";
    static final public String PARALLEL_CONN = "parallelConnections";
    static final public String TCP_WINDOW_SIZE = "tcpWindowSize";
    static final public String TCP_DYN_WINDOW_SIZE = "tcpDynamicWindowSize";
    static final public String UDP_BANDWIDTH = "udpBandwidth";
    
    //Measurement only
    static final public String SOURCE_HOSTNAME = "sourceHostname";
    static final public String SOURCE_IP = "sourceIP";
    static final public String DESTINATION_HOSTNAME = "destinationHostname";
    static final public String DESTINATION_IP = "destinationIP";
    
    static final public String TOOL_IPERF = "iperf";
    static final public String TOOL_NUTTCP = "nuttcp";
    static final public String TOOL_THRULAY = "thrulay";
    static final public String PROTOCOL_TCP = "tcp";
    static final public String PROTOCOL_UDP = "udp";

    public static final String IPV_V6_ONLY = "v6-only";
    public static final String IPV_V4_ONLY = "v4-only";
    public static final String IPV_PREFER_V6 = "prefer-v6";
}
