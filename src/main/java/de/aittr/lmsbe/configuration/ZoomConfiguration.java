package de.aittr.lmsbe.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "zoom")
@Data
public class ZoomConfiguration {
    private String accountId;
    private String clientId;
    private String clientSecret;
    private String secretHookToken;
}
