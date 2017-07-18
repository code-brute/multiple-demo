package com.example.resource;

import com.example.service.SqsIntegrationJmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Jackson on 2017/7/18.
 */
@RestController
@RequestMapping(value = "/api/aws/")
public class SqsIntegrationJmsResource {
  
  @Autowired
  private SqsIntegrationJmsService sqsIntegrationJmsService;
  
  @RequestMapping(
      value = "jms/fifoQueue",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String createFifoQueue() {
    return sqsIntegrationJmsService.createFifoQueue();
  }
  
  @RequestMapping(
      value = "jms/standardQueue",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String createStandardQueue() {
    return sqsIntegrationJmsService.createStandardQueue();
  }
  
  @RequestMapping(
      value = "jms/syncStandardMessage",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public void standSendMessageAndAutoAndSync() {
    sqsIntegrationJmsService.standSendMessageAndAutoAndSync();
  }
  
  @RequestMapping(
      value = "jms/asyncStandardMessage",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public void standSendMessageAndClientAndAsync() {
    sqsIntegrationJmsService.standSendMessageAndClientAndSync();
  }
}
