package com.shopizer.rabbitmq;

import com.shopizer.constant.Constant;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class MessageProducer {

  private final RabbitTemplate rabbitTemplate;

  public MessageProducer(RabbitTemplate rabbitTemplate) {
    this.rabbitTemplate = rabbitTemplate;
  }

  public void send(String exchange, String routingKey, String message) {
    MessageProperties props = new MessageProperties();
    props.setMessageId(MDC.get(Constant.TRACE_ID));

    rabbitTemplate.convertAndSend(exchange, routingKey, new Message(message.getBytes(StandardCharsets.UTF_8), props));
  }

  public void send(String exchange, String routingKey, int retry, Message message) {
    message.getMessageProperties().getHeaders().put("x-retry-count", retry + 1);
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

}