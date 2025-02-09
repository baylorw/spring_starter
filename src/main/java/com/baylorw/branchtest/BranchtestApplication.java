package com.baylorw.branchtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@ConfigurationPropertiesScan    // Allow creation of property files from application.yaml
@EnableCaching
public class BranchtestApplication {

    public static void main(String[] args) {
        SpringApplication.run(BranchtestApplication.class, args);
    }

}
