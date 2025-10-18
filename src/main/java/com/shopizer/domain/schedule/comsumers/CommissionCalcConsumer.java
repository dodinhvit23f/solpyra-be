package com.shopizer.domain.schedule.comsumers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.shopizer.domain.schedule.dto.CommissionMessage;
import com.shopizer.domain.schedule.services.CommissionSchedulerService;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommissionCalcConsumer {

  final CommissionSchedulerService commissionSchedulerService;
  final ObjectMapper objectMapper;

  @RabbitListener(queues = "${rabbitmq.queues.commission-clac-callback.name}")
  public void receiveCommissionCallBack(Message message, Channel channel) throws IOException {
    long tag = message.getMessageProperties().getDeliveryTag();
    String messageId = message.getMessageProperties().getMessageId();
    new CommissionMessage();
    CommissionMessage commissionMessage;

    log.info("Received commission message with id {}", messageId);
    int retryCount = (Integer) message.getMessageProperties()
        .getHeaders()
        .getOrDefault("x-retry-count", 0);

    try {
      log.info("Handling message calc commission with id {}", messageId);
      commissionMessage = objectMapper.readValue(message.getBody(), CommissionMessage.class);
      commissionMessage.setRetry(retryCount);

      if (Objects.isNull(commissionMessage.getStatus())) {
        channel.basicReject(tag, false);
        return;
      }

      commissionSchedulerService.processCallbackMessages(commissionMessage);
      channel.basicAck(tag, false);
      log.info("Finished commission message with id {}", messageId);
    } catch (Exception e) {
      log.error("Error at handle calc commission id {}", messageId, e);
      channel.basicNack(tag, false, true);
    }
  }

}
