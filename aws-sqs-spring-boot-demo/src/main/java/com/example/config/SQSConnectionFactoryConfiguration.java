package com.example.config;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Jackson on 2017/7/18.
 */
@Configuration
@EnableAutoConfiguration
public class SQSConnectionFactoryConfiguration {
  
  private static final String accessKey = "AKIAJK5HYPF27XUV6KFQ";
  private static final String accessSecret = "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";
  
  @Bean
  public SQSConnectionFactory sqsConnectionFactory() {
    final AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecret);
    // Create the connection factory using the environment variable credential provider.
    // Connections this factory creates can talk to the queues in us-east-2 region.
    return SQSConnectionFactory.builder()
        .withRegion(Region.getRegion(Regions.US_EAST_2))
        .withAWSCredentialsProvider(new AWSStaticCredentialsProvider(awsCredentials))
        .build();
  }
}
