package com.example.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by Jackson on 2017/7/17.
 */
@Configuration
@EnableAutoConfiguration
public class AwsSqsConfig {
  private static final String accessKey = "AKIAJK5HYPF27XUV6KFQ";
  private static final String accessSecret = "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";
  
  @Bean
  public AmazonSQS amazonSQSClient() {
    final AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,accessSecret);
    return AmazonSQSClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
        .withRegion(Regions.US_EAST_2)
        .build();
    // return new AmazonSQSClient(awsCredentials);
  }
}
