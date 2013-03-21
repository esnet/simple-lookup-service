//package net.es.lookup.client;
//
//import java.io.IOException;
//import java.net.Socket;
//import net.es.lookup.common.ReservedKeywords;
//
///**
// * User: sowmya
// * Date: 1/3/13
// * Time: 3:19 PM
// */
//public class SimpleLS {
//    private String url = "";
//    private String host = "";
//    private int port = 0;
//    private int timeout = 30; //seconds
//    private String connectionType = "GET"; //HTTP connection type
//    private String data= "";
//    private String url = "";
//    private String status = ReservedKeywords.SERVER_STATUS_UNKNOWN;
//    private double latency =  0.0;
//
//
//
//    SimpleLS(String host, int port){
//        this.host = host;
//        this.port = port;
//        this.url = "http://"+host+":"+port;
//    }
//
//    SimpleLS(String host, int port, String connectionType, String data, int timeout){
//        this.host = host;
//        this.port = port;
//        this.connectionType = connectionType;
//        this.data = data;
//        this.timeout = timeout;
//        this.url = "http://"+host+":"+port;
//    }
//
//
//    public String getUrl() {
//
//        return url;
//    }
//
//    public void setUrl(String url) {
//
//        this.url = url;
//    }
//
//    public String getHost() {
//
//        return host;
//    }
//
//    public void setHost(String host) {
//
//        this.host = host;
//    }
//
//    public int getPort() {
//
//        return port;
//    }
//
//    public void setPort(int port) {
//
//        this.port = port;
//    }
//
//    public int getTimeout() {
//
//        return timeout;
//    }
//
//    public void setTimeout(int timeout) {
//
//        this.timeout = timeout;
//    }
//
//    public String getConnectionType() {
//
//        return connectionType;
//    }
//
//    public void setConnectionType(String connectionType) {
//
//        this.connectionType = connectionType;
//    }
//
//    public String getData() {
//
//        return data;
//    }
//
//    public void setData(String data) {
//
//        this.data = data;
//    }
//
//
//    public void connect(){
//
//        try {
//            long start = System.nanoTime();
//            Socket socket = new Socket(this.host, this.port);
//
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//    }
//
//
//}
