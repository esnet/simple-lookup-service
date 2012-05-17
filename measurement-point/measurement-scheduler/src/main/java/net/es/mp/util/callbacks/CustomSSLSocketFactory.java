package net.es.mp.util.callbacks;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.glassfish.grizzly.ssl.SSLContextConfigurator;

/**
 * Custom SSL Socket Factory that allows keystores to be assigned by constructor
 * 
 * @author Andy Lake<alake@es.net>
 *
 */
public class CustomSSLSocketFactory implements ProtocolSocketFactory{
    SSLContext sslContext;

    public CustomSSLSocketFactory(String keystore, String keystorePassword){
        SSLContextConfigurator sslContextConfig = new SSLContextConfigurator();
        sslContextConfig.setKeyStoreFile(keystore);
        sslContextConfig.setKeyStorePass(keystorePassword);
        sslContextConfig.setTrustStoreFile(keystore);
        sslContextConfig.setTrustStorePass(keystorePassword);
        this.sslContext = sslContextConfig.createSSLContext();
    }

    public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
            throws IOException, UnknownHostException {
        return this.sslContext.getSocketFactory().createSocket(socket,host,port,autoClose);
    }

    public Socket createSocket(String host, int port) throws IOException,UnknownHostException {
        return this.sslContext.getSocketFactory().createSocket(host,port);
    }

    public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort) throws IOException, UnknownHostException {
        return this.sslContext.getSocketFactory().createSocket(host,port,clientHost,clientPort);
    }

    public Socket createSocket(String host, int port, InetAddress localAddress,
            int localPort, HttpConnectionParams params ) throws IOException, UnknownHostException, ConnectTimeoutException {
        if (params == null) {
            throw new IllegalArgumentException("Parameters may not be null");
        }
        int timeout = params.getConnectionTimeout();
        SocketFactory socketfactory = this.sslContext.getSocketFactory();
        if (timeout == 0) {
            return socketfactory.createSocket(host, port, localAddress, localPort);
        } else {
            Socket socket = socketfactory.createSocket();
            SocketAddress localaddr = new InetSocketAddress(localAddress, localPort);
            SocketAddress remoteaddr = new InetSocketAddress(host, port);
            socket.bind(localaddr);
            socket.connect(remoteaddr, timeout);
            return socket;
        }
    }



}
