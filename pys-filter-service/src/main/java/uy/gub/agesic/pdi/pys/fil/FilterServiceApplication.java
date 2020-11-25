package uy.gub.agesic.pdi.pys.fil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableCircuitBreaker
@EnableEurekaClient
@SpringBootApplication
@EnableMongoAuditing
@EnableMongoRepositories(basePackages = "uy.gub.agesic.pdi.pys.backend.repository")
public class FilterServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilterServiceApplication.class, args);
    }

}
