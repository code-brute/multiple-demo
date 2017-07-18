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
        //在此模式下，当确认某条消息时，也会隐式确认在该消息之前收到的所有消息。
        // 例如，如果收到 10 条消息，
        // 则仅确认第 10 条消息 (按接收消息的顺序)，然后确认先前的所有 9 条消息。
        message.acknowledge();//收到消息时，显示消息，然后显式确认消息。显示的确认消息后，消息才会被删除
        logger.info("Async {}",message.getJMSMessageID());
      }
    } catch (JMSException e) {
      logger.error(e.getMessage(),e);
    }
  }
}
