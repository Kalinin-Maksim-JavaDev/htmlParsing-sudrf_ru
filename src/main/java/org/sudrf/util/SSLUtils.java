/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sudrf.util;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.util.logging.Logger.getLogger;
import javax.net.ssl.*;
import static javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier;
import static javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory;
import static javax.net.ssl.SSLContext.getInstance;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author r.i.konoplev
 */
public class SSLUtils {

    private static final Logger LOG = getLogger(SSLUtils.class.getName());

    /**
     *
     * @throws KeyManagementException
     * @throws NoSuchAlgorithmException
     */
    public static void enableSSLSocket() throws KeyManagementException, NoSuchAlgorithmException {
        setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });

        SSLContext context = getInstance("TLS");
        context.init(null, new X509TrustManager[]{new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }
        }}, new SecureRandom());
        setDefaultSSLSocketFactory(context.getSocketFactory());
    }

    private SSLUtils() {
    }

    

        public static void main(String[] args) throws KeyManagementException, NoSuchAlgorithmException {
            String url = "http://sudrf.ru/index.php?id=300&act=go_ms_search&searchtype=ms&var=true&ms_type=ms&court_subj=4&ms_city=&ms_street=";

            enableSSLSocket();
            try {

                Document doc = Jsoup.connect(url).get();
            } catch (IOException ex) {
                Logger.getLogger(SSLUtils.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    

}
