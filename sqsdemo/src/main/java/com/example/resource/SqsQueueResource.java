package com.example.resource;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.example.domain.SqsQueueListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Jackson on 2017/7/17.
 */
@RestController
@RequestMapping(value = "/api/aws")
public class SqsQueueResource {
  
  private final static String queueUrl = "https://sqs.us-east-2.amazonaws.com/486517773544/MyQueue-jackson";
  private final static String fifoQueueUrl = "https://sqs.us-east-2.amazonaws.com/486517773544/MyFifoQueue.fifo";
  
  @Autowired
  AmazonSQS amazonSQS;
  
  @Autowired
  SqsQueueListener sqsQueueListener;
  
  @RequestMapping(value = "fifoQueue", method = RequestMethod.GET)
  public String createFifoQueue() {
    // Create a FIFO queue
    System.out.println("Creating a new Amazon SQS FIFO queue called MyFifoQueue.fifo.\n");
    Map<String, String> attributes = new HashMap<String, String>();
// A FIFO queue must have the FifoQueue attribute set to True
    attributes.put("FifoQueue", "true");
// Generate a MessageDeduplicationId based on the content, if the user doesn't provide a MessageDeduplicationId
    attributes.put("ContentBasedDeduplication", "true");
// The FIFO queue name must end with the .fifo suffix
    CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyFifoQueue.fifo").withAttributes(attributes);
    String myQueueUrl = amazonSQS.createQueue(createQueueRequest).getQueueUrl();
    return myQueueUrl;
  }
  
  @RequestMapping(value = "standardQueue", method = RequestMethod.GET)
  public String createStandardQueue() {
    // Create a queue
    System.out.println("Creating a new SQS queue called MyQueue.\n");
    CreateQueueRequest createQueueRequest = new CreateQueueRequest().withQueueName("MyQueue-jackson");
    String myQueueUrl = amazonSQS.createQueue(createQueueRequest).getQueueUrl();
    return myQueueUrl;
  }
  
  @RequestMapping(value = "deleteQueue", method = RequestMethod.GET)
  public String deleteQueues() {
    ListQueuesResult listQueuesResult = amazonSQS.listQueues();
    System.out.println("Your SQS Queue URLs:");
    for (String url : listQueuesResult.getQueueUrls()) {
      System.out.println(url);
      amazonSQS.deleteQueue(url);
    }
    return "deleteSuccess";
  }
  
  @RequestMapping(value = "listQueue", method = RequestMethod.GET)
  public String getQueueUrl() {
    ListQueuesResult listQueuesResult = amazonSQS.listQueues();
    System.out.println("Your SQS Queue URLs:");
    List list = new ArrayList();
    for (String url : listQueuesResult.getQueueUrls()) {
      System.out.println(url);
      list.add(url);
    }
    return list.toString();
  }
  
  @RequestMapping(value = "sendStand", method = RequestMethod.GET)
  public String sendMessage() {
    // Send a message
    System.out.println("Sending a message to MyQueue.\n");
    amazonSQS.sendMessage(new SendMessageRequest()
        .withQueueUrl(queueUrl)
        .withMessageBody("This is my message text."));
    return "success";
  }
  
  @RequestMapping(value = "sendFifo", method = RequestMethod.GET)
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
    return "fifoQueue";
  }
  
  @RequestMapping(value = "receive", method = RequestMethod.GET)
  public String receiveMessage() {
    ReceiveMessageResult r = amazonSQS.receiveMessage(queueUrl);
    System.out.println(r);
    for (Message m : r.getMessages()) {
      System.out.println(" [messageBody] : " + m.getBody() + "  [messageId] :" + m.getMessageId());
    }
    return "dd";
  }
}
