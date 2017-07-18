// package com.example;
//
// import static org.hamcrest.Matchers.equalTo;
// import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
// import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//
// import com.amazonaws.services.sqs.AmazonSQSClient;
// import com.example.domain.SqsQueueListener;
// import com.example.resource.SqsQueueResource;
// import org.junit.Before;
// import org.junit.Test;
// import org.mockito.InjectMocks;
// import org.mockito.MockitoAnnotations;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.test.util.ReflectionTestUtils;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.RequestBuilder;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
// /**
//  * Created by Jackson on 2017/7/17.
//  */
// public class SendTest extends SqsdemoApplicationTests{
//
//   private Logger LOG = LoggerFactory.getLogger(SendTest.class);
//   private MockMvc mvc;
//
//   @InjectMocks
//   AmazonSQSClient amazonSQS;
//
//   @InjectMocks
//   SqsQueueListener sqsQueueListener;
//
//   @Before
//   public void setUp() throws Exception {
//     MockitoAnnotations.initMocks(this);
//     SqsQueueResource sqsQueueResource =   new SqsQueueResource();
//     ReflectionTestUtils.setField(sqsQueueResource, "amazonSQS", amazonSQS);
//     ReflectionTestUtils.setField(sqsQueueResource, "sqsQueueListener", sqsQueueListener);
//     mvc = MockMvcBuilders.standaloneSetup(sqsQueueResource).build();
//   }
//
//   @Test
//   public void send() throws Exception {
//     RequestBuilder request;
//     request = get("/api/aws/send");
//     mvc.perform(request)
//         .andExpect(content().string(equalTo("send")));
//   }
// }
