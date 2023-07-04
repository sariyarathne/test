package com.sysco.perso.analytics.config;

import com.amazonaws.auth.EC2ContainerCredentialsProviderWrapper;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.sysco.perso.analytics.client.statemachine.impl.AwsS3UploadOfferTrackerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OfferTrackerConfig {

  @Bean
  public AmazonS3 s3Client() {
    return AmazonS3ClientBuilder
            .standard()
            .withCredentials(new EC2ContainerCredentialsProviderWrapper())
            .withRegion("us-east-1")
            .build();
  }

  @Bean
  public AwsS3UploadOfferTrackerClient awsS3offerClient(final AmazonS3 s3Client) {
    return new AwsS3UploadOfferTrackerClient(s3Client);
  }

}
