package com.shopizer.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopizer.constant.Constant;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {

  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  public void send(String exchange, String routingKey, Object message)
      throws JsonProcessingException {
    MessageProperties props = new MessageProperties();
    props.setMessageId(MDC.get(Constant.TRACE_ID));

    rabbitTemplate.convertAndSend(exchange, routingKey,
        new Message(objectMapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8),
            props));
  }

  public void send(String exchange, String routingKey, int retry, Message message) {
    message.getMessageProperties().getHeaders().put("x-retry-count", retry + 1);
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

}