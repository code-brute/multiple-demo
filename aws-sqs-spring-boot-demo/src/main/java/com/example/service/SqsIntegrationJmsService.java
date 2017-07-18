package com.example.service;

import org.springframework.stereotype.Service;

/**
 * Created by Jackson on 2017/7/18.
 */
@Service
public interface SqsIntegrationJmsService {
  
  String createFifoQueue();
  
  String createStandardQueue();
  
  void standSendMessage();
}
