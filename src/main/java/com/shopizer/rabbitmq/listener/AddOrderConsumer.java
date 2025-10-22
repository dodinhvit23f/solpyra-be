package com.shopizer.rabbitmq.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.shopizer.configuration.RabbitQueuesProperties;
import com.shopizer.configuration.RabbitQueuesProperties.QueueProperties;
import com.shopizer.domain.user.dto.request.UserAddOrderRequest;
import com.shopizer.domain.user.services.UserShopeeOrderService;
import com.shopizer.exception.NotFoundException;
import com.shopizer.rabbitmq.MessageProducer;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AddOrderConsumer {

  final ObjectMapper objectMapper;
  final MessageProducer messageProducer;
  final RabbitQueuesProperties rabbitQueuesProperties;
  final UserShopeeOrderService userShopeeOrderService;

  @RabbitListener(queues = "${rabbitmq.queues.add-order.name}")
  public void receive(Message message, Channel channel) throws IOException {
    String messageId = message.getMessageProperties().getMessageId();
    UserAddOrderRequest request = null;
    log.info("Received add order message: {}", messageId);
    long tag = message.getMessageProperties().getDeliveryTag();
    int retryCount = (Integer) message.getMessageProperties()
        .getHeaders()
        .getOrDefault("x-retry-count", 0);

    try {
      request = objectMapper.readValue(message.getBody(), UserAddOrderRequest.class);
      userShopeeOrderService.mapOrderForUser(request);
    } catch (JsonProcessingException e) {
      log.error("Error processing add order message {}", messageId, e);
    } catch (BadRequestException e) {
      log.error("User {} tried to take assigned order ID {} ", request.getUserName(), request.getOrderCode(), e);
    } catch (NotFoundException e) {
      if (retryCount < 72) {
        log.info("Sending retry message {} count {}  ", messageId, retryCount);
        QueueProperties retry = rabbitQueuesProperties.getAddOrderRetry();
        messageProducer.send(retry.getExchange(), retry.getRoutingKey(), retryCount, message);
      }
    } finally {
      channel.basicAck(tag, false);
    }


  }

}