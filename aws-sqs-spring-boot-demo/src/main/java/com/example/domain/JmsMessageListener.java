package com.example.domain;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Jackson on 2017/7/18.
 */
public class JmsMessageListener  implements MessageListener {
  
  Logger logger = LoggerFactory.getLogger(JmsMessageListener.class);
  
  @Override
  public void onMessage(Message message) {
    try {
      // Cast the received message as TextMessage and print the text to screen.
      if (message != null) {
        logger.info("Async Received: " + ((TextMessage) message).getText());
      }
    } catch (JMSException e) {
      logger.error(e.getMessage(),e);
    }
  }
}
