package com.example.service.impl;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.example.service.SqsIntegrationJmsService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.jms.JMSException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Jackson on 2017/7/18.
 */
@Service
public class SqsIntegrationJmsServiceImpl implements SqsIntegrationJmsService{
  
  Logger logger = LoggerFactory.getLogger(SqsIntegrationJmsServiceImpl.class);
  
  @Autowired
  private SQSConnectionFactory sqsConnectionFactory;
  
  /**
   * 如果您使用 getWrappedAmazonSQSClient，则返回的客户端对象会将所有异常转变成 JMS 异常。
   * 果您使用 getAmazonSQSClient，则这些异常将为 Amazon SQS 异常。
   * @return
   */
  @Override
  public String createFifoQueue() {
    // Create the connection.
    CreateQueueResult createQueueResult;
    String queueUrl = null;
    SQSConnection connection = null;
    try {
      connection = sqsConnectionFactory.createConnection();
      // Get the wrapped client
      AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
      Map<String, String> attributes = new HashMap<String, String>();
      // A FIFO queue must have the FifoQueue attribute set to True
      attributes.put("FifoQueue", "true");
      // Generate a MessageDeduplicationId based on the content, if the user doesn't provide a MessageDeduplicationId
      attributes.put("ContentBasedDeduplication", "true");
      // Create an SQS queue named 'TestQueue' – if it does not already exist.
      if (!client.queueExists("TestQueue")) {
        createQueueResult = client.createQueue(new CreateQueueRequest("TestQueue.fifo").withAttributes(attributes));
        queueUrl = createQueueResult.getQueueUrl();
      }
    } catch (JMSException e) {
      logger.error(e.getMessage(), e);
    }finally {
      closeConnection(connection);
    }
    return queueUrl;
  }
  
  @Override
  public String createStandardQueue() {
   CreateQueueResult createQueueResult;
   SQSConnection sqsConnection = null;
   String queueUrl = null;
    try {
      sqsConnection = sqsConnectionFactory.createConnection();
      AmazonSQSMessagingClientWrapper amazonSQS =  sqsConnection.getWrappedAmazonSQSClient();
      if (!amazonSQS.queueExists("TestQueueStand")) {
        createQueueResult = amazonSQS.createQueue(new CreateQueueRequest().withQueueName("TestQueueStand"));
        queueUrl = createQueueResult.getQueueUrl();
      }
    } catch (JMSException e) {
      logger.error(e.getMessage(),e);
    }finally {
      closeConnection(sqsConnection);
    }
    return queueUrl;
  }
  
  private void closeConnection(SQSConnection sqsConnection) {
    if (Objects.nonNull(sqsConnection)) {
      try {
        sqsConnection.close();
      } catch (JMSException e) {
        logger.error(e.getMessage(),e);
      }
    }
  }
}
