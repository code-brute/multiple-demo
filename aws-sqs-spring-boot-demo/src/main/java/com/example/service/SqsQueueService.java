package com.example.service;

import com.amazonaws.services.sqs.model.SendMessageResult;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Created by Jackson on 2017/7/18.
 */
@Service
public interface SqsQueueService {
  
  String createFifoQueue();
  
  String createStandardQueue();
  
  List<String> deleteQueues();
  
  List<String> getListQueues();
  
  SendMessageResult sendStandMessage();
  
  String sendFifoMessage();
  
  List<String> standReceiveMessage();
  
  String standDeleteMessage();
  
  List<String> fifoReceiveMessage();
  
  List<String> fifoDeleteMessage();
}
