package com.solpyra.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.solpyra.common.constant.Constant;
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

  public static final String X_RETRY_COUNT = "x-retry-count";
  private final RabbitTemplate rabbitTemplate;
  private final ObjectMapper objectMapper;

  public void send(String exchange, String routingKey, Object message)
      throws JsonProcessingException {
    MessageProperties props = new MessageProperties();
    props.setMessageId(MDC.get(Constant.TRACE_ID));
    props.setHeader(X_RETRY_COUNT, 0);

    byte[] body = objectMapper.writeValueAsString(message).getBytes(StandardCharsets.UTF_8);
    Message msg = new Message(body, props);

    rabbitTemplate.convertAndSend(exchange, routingKey,msg);
  }

  public void send(String exchange, String routingKey, int retry, Message message) {
    message.getMessageProperties().getHeaders().put(X_RETRY_COUNT, retry + 1);
    rabbitTemplate.convertAndSend(exchange, routingKey, message);
  }

}