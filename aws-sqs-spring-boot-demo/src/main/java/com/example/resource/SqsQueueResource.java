package com.example.resource;

import com.example.service.impl.SqsQueueServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Jackson on 2017/7/17.
 */
@RestController
@RequestMapping(value = "/api/aws")
public class SqsQueueResource {
  
  
  @Autowired
  SqsQueueServiceImpl sqsQueueService;
  
  @RequestMapping(
      value = "fifoQueue",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String createFifoQueue() {
    return sqsQueueService.createFifoQueue();
  }
  
  @RequestMapping(
      value = "standardQueue",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String createStandardQueue() {
    return sqsQueueService.createStandardQueue();
  }
  
  @RequestMapping(
      value = "Queues",
      method = RequestMethod.DELETE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String>  deleteQueues() {
    return sqsQueueService.deleteQueues();
  }
  
  @RequestMapping(value = "Queues",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> getListQueues() {
    return sqsQueueService.getListQueues();
  }
  
  @RequestMapping(
      value = "standMessage",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String sendStandMessage() {
    return sqsQueueService.sendStandMessage().toString();
  }
  
  @RequestMapping(
      value = "fifoMessage",
      method = RequestMethod.POST,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public String sendFifoMessage() {
    return sqsQueueService.sendFifoMessage();
  }
  
  @RequestMapping(value = "receives",
      method = RequestMethod.GET,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public List<String> receiveMessage() {
    return sqsQueueService.receiveMessage();
  }
  
}
