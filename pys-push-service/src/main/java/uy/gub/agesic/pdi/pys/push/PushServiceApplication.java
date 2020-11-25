package uy.gub.agesic.pdi.pys.push;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(
        scanBasePackages = {"uy.gub.agesic.pdi.pys", "uy.gub.agesic.pdi.common", "uy.gub.agesic.pdi.services.httpproxy"}
)
@EnableEurekaClient
@EnableFeignClients
@EnableCircuitBreaker
@EnableCaching
@EnableAspectJAutoProxy
@EnableMongoRepositories(basePackages = "uy.gub.agesic.pdi.pys.backend.repository")
public class PushServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PushServiceApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean routerServletBean(uy.gub.agesic.pdi.pys.push.controller.PushEndpoint pushEndpoint) {
        ServletRegistrationBean bean = new ServletRegistrationBean(pushEndpoint, "/push/*");
        bean.setLoadOnStartup(1);
        return bean;
    }

}
