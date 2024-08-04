package de.aittr.lmsbe.configuration;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class S3Client {

    private final AwsS3Configuration configuration;

    public S3Client(AwsS3Configuration configuration) {
        this.configuration = configuration;
    }

    @Bean
    public AmazonS3 createS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(
                configuration.getAccessKey(),
                configuration.getSecretKey()
        );

        AwsClientBuilder.EndpointConfiguration endpointConfiguration = new AwsClientBuilder.EndpointConfiguration(
                configuration.getServiceEndpoint(),
                configuration.getSigningRegion()
        );
        AmazonS3ClientBuilder builder = AmazonS3ClientBuilder
                .standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials));
        builder.setEndpointConfiguration(endpointConfiguration);
        return builder.build();
    }

}
