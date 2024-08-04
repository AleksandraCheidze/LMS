package de.aittr.lmsbe.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Data
public class AwsS3Configuration {
    private String accessKey;
    private String secretKey;
    private String serviceEndpoint;
    private String signingRegion;
}
