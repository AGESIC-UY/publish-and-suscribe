package uy.gub.agesic.pdi.pys.pull;

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
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import uy.gub.agesic.pdi.pys.common.util.WSApplicationUtil;
import uy.gub.agesic.pdi.pys.pull.controller.PullEndpoint;

@SpringBootApplication(
        scanBasePackages = {"uy.gub.agesic.pdi.pys", "uy.gub.agesic.pdi.common", "uy.gub.agesic.pdi.services.httpproxy"}
)
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableCaching
@EnableAspectJAutoProxy
@EnableMongoRepositories(basePackages = "uy.gub.agesic.pdi.pys.backend.repository")
public class PullServiceApplication {

    @Value("${application.pull-service.https-port:22443}")
    private String httpsPort;

    @Value("${application.pull-service.key-store:classpath:keystore.jks}")
    private String keystorePath;

    @Value("${application.pull-service.key-store-password:password}")
    private String keystorePassword;

    private String httpsEnabled = System.getProperty("application.pull-service.https-enabled");

    public static void main(String[] args) {
        SpringApplication.run(PullServiceApplication.class, args);
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
    public ServletRegistrationBean routerServletBean(PullEndpoint pullEndpoint) {
        PullEndpoint.accetpHttpsOnly(httpsEnabled != null);
        ServletRegistrationBean bean = new ServletRegistrationBean(pullEndpoint, "/pull");
        bean.setLoadOnStartup(1);
        return bean;
    }
}
