package io.github.lbernau.bistro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.integration.config.EnableIntegration;

@EnableIntegration
@SpringBootApplication
@ConfigurationPropertiesScan(value = "io.github.lbernau.bistro")
public class DeichmannBistroApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeichmannBistroApplication.class, args);
    }

}
