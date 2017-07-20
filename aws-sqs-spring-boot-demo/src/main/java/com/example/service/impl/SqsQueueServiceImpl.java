package com.example.service.impl;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageBatchRequest;
import com.amazonaws.services.sqs.model.SendMessageBatchRequestEntry;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.example.service.SqsQueueService;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by Jackson on 2017/7/18.
 */
@Service
public class SqsQueueServiceImpl implements SqsQueueService{
  
  Logger logger = LoggerFactory.getLogger(SqsQueueServiceImpl.class);
  
  @Autowired
  AmazonSQS amazonSQS;
  
  private final static String queueUrl = "https://sqs.us-east-2.amazonaws.com/486517773544/MyQueue-jackson";
  private final static String fifoQueueUrl = "https://sqs.us-east-2.amazonaws.com/486517773544/MyFifoQueue.fifo";
  
  @Override
  public String createFifoQueue() {
    // Create a FIFO queue
    logger.info("Creating a new Amazon SQS FIFO queue called MyFifoQueue.fifo.\n");
    Map<String, String> attributes = new HashMap<String, String>();
    // A FIFO queue must have the FifoQueue attribute set to True
    attributes.put("FifoQueue", "true");// 将队列指定为FIFO Queue 您只能在创建队列时提供此属性。您不能更改现有队列。设置此属性时，还必须MessageGroupId明确提供您的消息。
    // Generate a MessageDeduplicationId based on the content, if the user doesn't provide a MessageDeduplicationId
    attributes.put("ContentBasedDeduplication", "true");// 使用消息正文的SH-256 哈希值(但不是消息的属性)来生成基于消息内容的重复数据的删除ID
    // 在此属性和 发送消息时指定的重复数据id冲突时以 指定的为准
    
    // 其他属性 和 标准队列的属性一样
    // The FIFO queue name must end with the .fifo suffix
    CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyFifoQueue.fifo").withAttributes(attributes);
    return amazonSQS.createQueue(createQueueRequest).getQueueUrl();
  }
  
  @Override
  public String createStandardQueue() {
    // Create a queue
    logger.info("Creating a new SQS queue called MyQueue.\n");
    Map<String, String> attributes = new HashMap<>();
    // 队列设置 以下这些设置 在AWS控制台皆可以看见和配置
    // 标准队列详细信息 请看http://docs.aws.amazon.com/zh_cn/AWSSimpleQueueService/latest/APIReference/API_CreateQueue.html
    attributes.put("DelaySeconds", "20");// 设置交付延时 有效值：0到900秒（15分钟）的整数。默认值为0（零）
    attributes.put("MessageRetentionPeriod", "60");// 设置消息保留周期  值必须在 1 分钟至 14 天之间 默认值为345600 (4day)
    attributes.put("ReceiveMessageWaitTimeSeconds","10");//设置接收消息等待时间 动作等待消息到达的时间长度（以秒为单位）。有效值：0到20（秒）之间的整数。默认值为0（零）
    attributes.put("MaximumMessageSize","256");//设置最大消息大小 值必须在 1 至 256 KB 之间。默认值是256
    attributes.put("VisibilityTimeout","40");//队列的可见性超时。有效值：从0到43,400（12小时）的整数。默认值为30.
    //死信队列设置
    attributes.put("RedrivePolicy", "true");// 在超过最大
    attributes.put("deadLetterTargetArn", "TestQueueStand");// 消息发往的队列，死信队列的名称
    attributes.put("maxReceiveCount", "10");//将消息发往死信队列之前允许接收该消息的最大次数 值必须在 1 至 1000 之间。
    CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName("MyQueue-jackson").withAttributes(attributes);
    return amazonSQS.createQueue(createQueueRequest).getQueueUrl();
  }
  
  @Override
  public List<String> deleteQueues() {
    List urlList = Lists.newArrayList();
    ListQueuesResult listQueuesResult = amazonSQS.listQueues();
    System.out.println("Your SQS Queue URLs:");
    for (String url : listQueuesResult.getQueueUrls()) {
      System.out.println(url);
      amazonSQS.deleteQueue(url);
      urlList.add(url);
    }
    return urlList;
  }
  
  @Override
  public List<String> getListQueues() {
    ListQueuesResult listQueuesResult = amazonSQS.listQueues();
    System.out.println("Your SQS Queue URLs:");
    List urlList = Lists.newArrayList();
    for (String url : listQueuesResult.getQueueUrls()) {
      System.out.println(url);
      urlList.add(url);
    }
    return urlList;
  }
  
  @Override
  public SendMessageResult sendStandMessage() {
    // Send a message
    System.out.println("Sending a message to MyQueue.\n");
    SendMessageBatchRequest sendMessageBatchRequest = new SendMessageBatchRequest().withEntries(new SendMessageBatchRequestEntry()
        .withMessageBody("223323").withId("61")
    ).withEntries(new SendMessageBatchRequestEntry()
        .withMessageBody("223323").withId("6")).withQueueUrl(queueUrl);
    amazonSQS.sendMessageBatch(sendMessageBatchRequest);
    return amazonSQS.sendMessage(new SendMessageRequest()
        .withQueueUrl(queueUrl)
        .withMessageBody("This is my message text."));
    
  }
  
  @Override
  public String sendFifoMessage() {
    // Send a message
    System.out.println("Sending a message to MyFifoQueue.fifo.\n");
    SendMessageRequest sendMessageRequest = new SendMessageRequest(fifoQueueUrl, "This is my message text.");
    // You must provide a non-empty MessageGroupId when sending messages to a FIFO queue
    sendMessageRequest.setMessageGroupId("messageGroup1");
    // Uncomment the following to provide the MessageDeduplicationId
    //sendMessageRequest.setMessageDeduplicationId("1");
    SendMessageResult sendMessageResult = amazonSQS.sendMessage(sendMessageRequest);
    String sequenceNumber = sendMessageResult.getSequenceNumber();
    String messageId = sendMessageResult.getMessageId();
    System.out.println("SendMessage succeed with messageId " + messageId + ", sequence number " + sequenceNumber + "\n");
    return messageId;
  }
  
  @Override
  public List<String> standReceiveMessage() {
    // 一种写法
    List<String> messageList = Lists.newArrayList();
    ReceiveMessageResult r = amazonSQS.receiveMessage(queueUrl);
    System.out.println(r);
    for (Message m : r.getMessages()) {
      String message = " [messageBody] : " + m.getBody() + "  [messageId] :" + m.getMessageId();
      System.out.println(message);
      messageList.add(message);
    }
    // 第二种写法
    System.out.println("Receiving messages from MyQueue.\n");
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(queueUrl);
    List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
    parseMessages(messages);
    System.out.println();
    return messageList;
  }
  
  /**
   * 如需指定要删除的消息，请提供您收到消息时 Amazon SQS 返回的接收句柄。
   * 您每个操作只能删除一条消息。
   * 要删除整个队列，必须使用 DeleteQueue 操作。(即使队列中包含消息，您也可以删除整个队列。)
   *
   * 如果您没有该消息的接收句柄，可以调用 ReceiveMessage 以再次接收消息。
   * 您每次收到消息时都会获得不同的接收句柄。
   * 使用 DeleteMessage 操作时，请使用最新的接收句柄。
   * 否则，可能无法从队列中删除您的消息。
   * @return
   */
  @Override
  public String standDeleteMessage() {
    ReceiveMessageResult r = amazonSQS.receiveMessage(queueUrl);
    System.out.println(r);
    List deleteMessageList = Lists.newArrayList();
    for (Message m : r.getMessages()) {
      String message = " [messageBody] : " + m.getBody() + "  [messageId] :" + m.getMessageId();
      // Delete a message
      System.out.println("Deleting a message.\n" + message);
      String messageReceiptHandle = m.getReceiptHandle();
      amazonSQS.deleteMessage(new DeleteMessageRequest()
          .withQueueUrl(queueUrl)
          .withReceiptHandle(messageReceiptHandle));
      deleteMessageList.add(message);
    }
    return deleteMessageList.toString();
  }
  
  @Override
  public List<String> fifoReceiveMessage() {
    // Receive messages
    System.out.println("Receiving messages from MyFifoQueue.fifo.\n");
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(fifoQueueUrl);
    // Uncomment the following to provide the ReceiveRequestDeduplicationId
    //receiveMessageRequest.setReceiveRequestAttemptId("1");
    List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
    return parseMessages(messages);
  }
  
  private List<String> parseMessages(List<Message> messages) {
    List<String> messageList = Lists.newArrayList();
    for (Message message : messages) {
      System.out.println("  Message");
      System.out.println("    MessageId:     " + message.getMessageId());
      System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
      System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
      System.out.println("    Body:          " + message.getBody());
      for (Entry<String, String> entry : message.getAttributes().entrySet()) {
        System.out.println("  Attribute");
        System.out.println("    Name:  " + entry.getKey());
        System.out.println("    Value: " + entry.getValue());
      }
      messageList.add("    MessageId:     " + message.getMessageId()+"    Body:          " + message.getBody());
    }
    return messageList;
  }
  
  @Override
  public List<String> fifoDeleteMessage() {
    // Delete the message
    List<String> messageList = Lists.newArrayList();
    System.out.println("Deleting the message.\n");
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(fifoQueueUrl);
    List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
    for (Message message : messages) {
      messageList.add(message.getMessageId() + message.getBody());
      String messageReceiptHandle = message.getReceiptHandle();
      amazonSQS.deleteMessage(new DeleteMessageRequest().withQueueUrl(fifoQueueUrl).withReceiptHandle(messageReceiptHandle));
    }
    return messageList;
  }

 
  private void sleep() {
    try {
      Thread.sleep(10);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
  
  @Async
  public void test2Message() {
    for(int n = 0 ; n < 10000; n++) {
      System.out.println("test2"+DateTime.now());
      System.out.println("test2 Receiving messages from MyFifoQueue.fifo.\n"+n);
      common();
    }
  }
  
  @Async
  public void test1Message() {
    for(int n = 0 ; n < 10000; n++) {
      System.out.println("test1+"+DateTime.now());
      System.out.println("test1 Receiving messages from MyFifoQueue.fifo.\n"+n);
      common();
    }
  }
  
  private void common() {
    String url = amazonSQS.createQueue("TestQueueStand").getQueueUrl();
    ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(url);
    List<Message> messages = amazonSQS.receiveMessage(receiveMessageRequest).getMessages();
    this.parseMessages(messages);
    sleep();
  }
  
}
