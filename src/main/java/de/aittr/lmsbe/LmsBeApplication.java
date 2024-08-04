package de.aittr.lmsbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableConfigurationProperties
@EnableAsync
public class LmsBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(LmsBeApplication.class, args);
    }

}
