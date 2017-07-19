package com.example.service.impl;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.example.domain.JmsMessageListener;
import com.example.service.SqsIntegrationJmsService;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Jackson on 2017/7/18.
 */
@Service
public class SqsIntegrationJmsServiceImpl implements SqsIntegrationJmsService {
  
  Logger logger = LoggerFactory.getLogger(SqsIntegrationJmsServiceImpl.class);
  
  @Autowired
  private SQSConnectionFactory sqsConnectionFactory;
  
  /**
   * 如果您使用 getWrappedAmazonSQSClient，则返回的客户端对象会将所有异常转变成 JMS 异常。
   * 果您使用 getAmazonSQSClient，则这些异常将为 Amazon SQS 异常。
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
    } finally {
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
      AmazonSQSMessagingClientWrapper amazonSQS = sqsConnection.getWrappedAmazonSQSClient();
      if (!amazonSQS.queueExists("TestQueueStand")) {
        createQueueResult = amazonSQS.createQueue(new CreateQueueRequest().withQueueName("TestQueueStand"));
        queueUrl = createQueueResult.getQueueUrl();
      }
    } catch (JMSException e) {
      logger.error(e.getMessage(), e);
    } finally {
      closeConnection(sqsConnection);
    }
    return queueUrl;
  }
  
  @Override
  public void standSendMessageAndAutoAndSync() {
    SQSConnection sqsConnection = null;
    try {
      sqsConnection = sqsConnectionFactory.createConnection();
      //将创建一个具有 AUTO_ACKNOWLEDGE 模式的非事务性 JMS 会话。
      Session session = sqsConnection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      // 为了向队列发送文本消息，将创建一个 JMS 队列标识和消息创建者。
      // Create a queue identity with name 'TestQueue' in the session
      Queue queue = session.createQueue("TestQueueStand");
      
      //Create a producer for the 'TestQueue'.
      MessageProducer producer = session.createProducer(queue);
      //创建文本消息并将它发送到队列。
      //create the text message.
      TextMessage textMessage = session.createTextMessage("Hello World");
      textMessage.setStringProperty("3","e");
      
      // Send the message.
      producer.send(textMessage);
      logger.info("JMS Message " + textMessage.getJMSMessageID());
      logger.info(textMessage.getStringProperty("3"));
      //同步接收消息
      syncReceiveMessage(sqsConnection, session, queue);
      //异步接收消息
      // asyncReceiveMessage(sqsConnection, session, queue);
      
    } catch (JMSException e) {
      logger.error(e.getMessage(), e);
    } finally {
      closeConnection(sqsConnection);
    }
  }
  
  @Override
  public void standSendMessageAndClientAndSync() {
    SQSConnection sqsConnection = null;
    try {
      sqsConnection = sqsConnectionFactory.createConnection();
      //将创建一个具有 具有 CLIENT_ACKNOWLEDGE 模式的会话 客户端显示的确认
      //在此模式下，当确认某条消息时，也会隐式确认在该消息之前收到的所有消息。
      // 例如，如果收到 10 条消息，
      // 则仅确认第 10 条消息 (按接收消息的顺序)，然后确认先前的所有 9 条消息。
      Session session = sqsConnection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
      // 为了向队列发送文本消息，将创建一个 JMS 队列标识和消息创建者。
      // Create a queue identity with name 'TestQueue' in the session
      Queue queue = session.createQueue("TestQueueStand");
      
      
      //Create a producer for the 'TestQueue'.
      MessageProducer producer = session.createProducer(queue);
      //创建文本消息并将它发送到队列。
      //create the text message.
      TextMessage textMessage = session.createTextMessage("Hello World");
      textMessage.setStringProperty("test","33333");
      // Send the message.
      textMessage.setJMSMessageID("1234567890"); // TODO 未生效 不明白
      producer.send(textMessage);
      logger.info("JMS Message " + textMessage.getJMSMessageID());
      //异步接收消息
      asyncReceiveMessage(sqsConnection, session, queue);
      
    } catch (JMSException e) {
      logger.error(e.getMessage(), e);
    } catch (InterruptedException e) {
      logger.error(e.getMessage(), e);
    } finally {
      closeConnection(sqsConnection);
    }
  }
  
  private void asyncReceiveMessage(SQSConnection sqsConnection, Session session, Queue queue) throws JMSException, InterruptedException {
    // 对 receive 实现实例设置使用者消息监听器，而不是对使用者显式调用 MyListener 方法。主线程会睡眠一秒钟。
    // Create a consumer for the 'TestQueue'.
    MessageConsumer consumer = session.createConsumer(queue);
    // Instantiate and set the message listener for the consumer.
    consumer.setMessageListener(new JmsMessageListener());
    // Start receiving incoming messages.
    sqsConnection.start();
    // Wait for 1 second. The listener onMessage() method will be invoked when a message is received.
    Thread.sleep(1000);
  }
  
  private void syncReceiveMessage(SQSConnection sqsConnection, Session session, Queue queue) throws JMSException {
    // 同步接收消息
    // Create a consumer for the 'TestQueue'.
    MessageConsumer consumer = session.createConsumer(queue);
    
    // Start receiving incoming messages.
    sqsConnection.start();
    // Receive a message from 'TestQueue' and wait up to 1 second
    Message receivedMessage = consumer.receive(1000);
    // Cast the received message as TextMessage and print the text to screen.
    if (receivedMessage != null) {
      logger.info("============"+receivedMessage.getStringProperty("3"));
      logger.info("Received: " + ((TextMessage) receivedMessage).getText());
    }
  }
  
  private void closeConnection(SQSConnection sqsConnection) {
    if (Objects.nonNull(sqsConnection)) {
      try {
        sqsConnection.close();
      } catch (JMSException e) {
        logger.error(e.getMessage(), e);
      }
    }
  }
}
