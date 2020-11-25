package uy.gub.agesic.pdi.pys.common.util;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

public class WSApplicationUtil {

    private WSApplicationUtil() {
    }

    public static Connector createSSLConnector(String httpsPort, String keystorePath, String keystorePassword) {
        Connector connector = new Connector(TomcatEmbeddedServletContainerFactory.DEFAULT_PROTOCOL);
        connector.setScheme("https");
        connector.setPort(Integer.parseInt(httpsPort));
        connector.setSecure(true);
        connector.setProperty("SSLEnabled", "true");
        connector.setProperty("keystoreFile", keystorePath);
        connector.setProperty("keystorePass", keystorePassword);
        connector.setProperty("clientAuth", "false");
        connector.setProperty("sslProtocol", "TLS");
        return connector;
    }
}
