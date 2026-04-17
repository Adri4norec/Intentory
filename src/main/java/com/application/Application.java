package com.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableJpaAuditing
@ConfigurationPropertiesScan
@ComponentScan(basePackages = {"com"})
@EnableJpaRepositories(basePackages = {"com"})
@EntityScan(basePackages = {"com"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}