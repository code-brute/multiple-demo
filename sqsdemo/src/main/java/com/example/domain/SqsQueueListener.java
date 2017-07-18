package com.example.domain;

import com.amazonaws.services.sqs.AmazonSQS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Jackson on 2017/7/17.
 */
@Component
public class SqsQueueListener {
  
  @Autowired
  AmazonSQS amazonSQS;
  
  public String test() {
    return "d";
  }
}
