package uy.gub.agesic.pdi.pys.pub;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import uy.gub.agesic.pdi.pys.common.util.WSApplicationUtil;
import uy.gub.agesic.pdi.pys.pub.controller.PubEndpoint;

@SpringBootApplication(
        scanBasePackages = {"uy.gub.agesic.pdi.pys", "uy.gub.agesic.pdi.common", "uy.gub.agesic.pdi.services.httpproxy"}
)
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableCaching
@EnableAspectJAutoProxy
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "uy.gub.agesic.pdi.pys.backend.repository")
public class PubServiceApplication {

    @Value("${application.pub-service.https-port:21443}")
    private String httpsPort;

    @Value("${application.pub-service.key-store:classpath:keystore.jks}")
    private String keystorePath;

    @Value("${application.pub-service.key-store-password:password}")
    private String keystorePassword;

    private String httpsEnabled = System.getProperty("application.pub-service.https-enabled");

    public static void main(String[] args) {
        SpringApplication.run(PubServiceApplication.class, args);
    }

    @Bean
    public EmbeddedServletContainerFactory servletContainer() {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        if (httpsEnabled != null) {
            tomcat.addAdditionalTomcatConnectors(WSApplicationUtil.createSSLConnector(httpsPort, keystorePath, keystorePassword));
        }
        return tomcat;
    }

    @Bean
    public ServletRegistrationBean routerServletBean(PubEndpoint pubEndpoint) {
        PubEndpoint.accetpHttpsOnly(httpsEnabled != null);
        ServletRegistrationBean bean = new ServletRegistrationBean(pubEndpoint, "/publicacion");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
