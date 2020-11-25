package uy.gub.agesic.pdi.pys.backoffice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(
        scanBasePackages = {"uy.gub.agesic.pdi.pys.backoffice", "uy.gub.agesic.pdi.common", "uy.gub.agesic.pdi"}
)
@EnableEurekaClient
@EnableFeignClients
@EnableDiscoveryClient
@EnableCaching
@EnableAspectJAutoProxy
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "uy.gub.agesic.pdi.pys.backend.repository")
public class BackofficePySApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackofficePySApplication.class, args);
    }
}

