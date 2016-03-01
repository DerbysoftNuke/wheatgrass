package com.derby.nuke.wheatgrass.util;

import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom

import javax.net.SocketFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

import org.slf4j.LoggerFactory

/**
 * AlwaysTrustSSLSocketFactory is the default ssl socket factory which
 * will use the AlwaysX509TrustManager to check the certification and use
 * the default rules to create the ssl socket factory.
 *
 * @author YangYang
 * @version 0.1, 2008-5-27 9:56:39
 * @see AlwaysX509TrustManager
 */
final class AlwaysTrustSSLSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory sslSocketFactory;

    public AlwaysTrustSSLSocketFactory() {
        try {
			TrustManager[] trustManager = new TrustManager[1];
			trustManager[0] = new AlwaysX509TrustManager();
		
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustManager, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            LoggerFactory.getLogger(this.getClass()).error("AlwaysTrustSSLSocketFactory init failed", e);
            throw new RuntimeException(e);
        } catch (KeyManagementException e) {
            LoggerFactory.getLogger(this.getClass()).error("AlwaysTrustSSLSocketFactory init failed", e);
            throw new RuntimeException(e);
        }
    }

    public String[] getDefaultCipherSuites() {
        return sslSocketFactory.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return sslSocketFactory.getSupportedCipherSuites();
    }

    public Socket createSocket(Socket socket, String s, int i, boolean b) throws IOException {
        return sslSocketFactory.createSocket(socket, s, i, b);
    }

    public Socket createSocket(String s, int i) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(s, i);
    }

    public Socket createSocket(String s, int i, InetAddress inetAddress, int i1) throws IOException, UnknownHostException {
        return sslSocketFactory.createSocket(s,i,inetAddress, i1);
    }

    public Socket createSocket(InetAddress inetAddress, int i) throws IOException {
        return sslSocketFactory.createSocket(inetAddress, i);
    }

    public Socket createSocket(InetAddress inetAddress, int i, InetAddress inetAddress1, int i1) throws IOException {
        return sslSocketFactory.createSocket(inetAddress, i, inetAddress1, i1);
    }

    public Socket createSocket() throws IOException {
        return sslSocketFactory.createSocket();
    }

    public static synchronized SocketFactory getDefault() {
        return new AlwaysTrustSSLSocketFactory();
    }
}

