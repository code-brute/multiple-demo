// package com.example;
//
// import com.amazonaws.services.sqs.AmazonSQSClient;
// import org.junit.Test;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.annotation.Autowired;
//
// /**
//  * Created by Jackson on 2017/7/17.
//  */
// public class RecvTest extends SqsdemoApplicationTests {
//
//   @Autowired
//   private AmazonSQSClient amazonSQSClient;
//
//   private Logger LOG = LoggerFactory.getLogger(RecvTest.class);
//
//   @Test
//   public void recv() {
//
//     // ReceiveMessageResult r = amazonSQSClient.receiveMessage(queueUrl);
//     // LOG.debug("RECV-RESULT = {}", r);
//     // for (Message m : r.getMessages()) {
//     //   LOG.info("MESSAGE({}) -- {}",
//     //       m.getMessageId(),
//     //       m.getBody());
//       //
//       // amazonSQSClient.deleteMessage(queueUrl, m.getReceiptHandle());
//     // }
//   }
// }
