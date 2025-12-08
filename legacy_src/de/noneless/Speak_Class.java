package de.noneless;

import java.io.IOException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Speak_Class {
    private static final Logger LOGGER = Logger.getLogger(Speak_Class.class.getName());
    private static final String SPEAK_URL = "https://noneless.de/currentTrack.php?key=Threams&name=";

    static {
        // SSL-Konfiguration
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "SSL-Konfiguration fehlgeschlagen", e);
        }
    }

    public static void Speak(String text) {
        if (text == null) return;
        String safeText = text.replace('"', ' ');
        String urlString = SPEAK_URL + safeText.replace(" ", "%20");
        HttpsURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            try (var is = connection.getInputStream()) {
                // InputStream wird automatisch geschlossen
            }
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Fehler beim Speak-Request", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}

