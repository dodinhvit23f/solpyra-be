package com.solpyra.domain.schedule.services;

import com.solpyra.configuration.RabbitQueuesProperties;
import com.solpyra.configuration.RabbitQueuesProperties.QueueProperties;
import com.solpyra.common.constant.CommissionOutboxStatus;
import com.solpyra.common.constant.Constant;
import com.solpyra.common.constant.OrderStatus;
import com.solpyra.domain.schedule.dto.CommissionMessage;
import com.solpyra.domain.schedule.dto.CommissionMessage.Order;
import com.solpyra.domain.schedule.repositories.CommissionOutboxRepository;
import com.solpyra.domain.schedule.repositories.ScheduleShopeeOrderRepository;
import com.solpyra.entities.CommissionOutbox;
import com.solpyra.entities.ShopeeOrder;
import com.solpyra.entities.UserPerShopeeOrder;
import com.solpyra.rabbitmq.MessageProducer;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommissionSchedulerServiceImpl implements CommissionSchedulerService {

  private final ScheduleShopeeOrderRepository orderRepository;
  private final CommissionOutboxRepository outboxRepository;
  private final MessageProducer producer;
  private final RabbitQueuesProperties rabbitQueuesProperties;


  @Override
  public void createOutboxForEligibleOrders() {
    ZonedDateTime cutoff = ZonedDateTime.of(LocalDate.now().minusDays(16).atStartOfDay(),
        ZoneOffset.systemDefault());
    List<UserPerShopeeOrder> eligibleOrders = orderRepository.findEligibleOrders(
        OrderStatus.COMPLETED, cutoff);
    log.info("Found {} orders eligible for commission", eligibleOrders.size());

    List<CommissionOutbox> outboxes = new LinkedList<>();
    List<ShopeeOrder> orders = new LinkedList<>();

    eligibleOrders.forEach(order -> {
      ShopeeOrder shopeeOrder = order.getOrder();
      CommissionOutbox outbox = CommissionOutbox.builder()
          .orderId(order.getOrderId())
          .userId(order.getUserId())
          .commission(shopeeOrder.getUserCommission())
          .status(CommissionOutboxStatus.PENDING)
          .createdAt(shopeeOrder.getCommissionedDate())
          .build();

      shopeeOrder.setStatus(OrderStatus.PENDING_COMMISSIONED);

      outboxes.add(outbox);
      orders.add(shopeeOrder);
    });

    if (!ObjectUtils.isEmpty(outboxes)) {
      outboxRepository.saveAll(outboxes);
      orderRepository.saveAll(orders);
    }
  }

  @Override
  public void processPendingMessages() {
    QueueProperties properties = rabbitQueuesProperties.getCommissionClac();
    Map<BigInteger, List<CommissionOutbox>> userCommissionOutBox = outboxRepository.findByStatusOrderByCreatedAt(
            List.of(CommissionOutboxStatus.PENDING, CommissionOutboxStatus.FAILED))
        .stream()
        .collect(Collectors.groupingBy(CommissionOutbox::getUserId));

    log.info("Processing {} pending commission outbox records", userCommissionOutBox.size());

    for (Entry<BigInteger, List<CommissionOutbox>> record : userCommissionOutBox.entrySet()) {
      try {
        // Build message (fetch order if needed)
        CommissionMessage message = CommissionMessage.builder()
            .id(UUID.randomUUID().toString())
            .userId(record.getKey())
            .orders(record.getValue()
                .stream()
                .map(
                    event ->  CommissionMessage.Order.builder()
                        .orderId(event.getOrderId())
                        .commissionAmount(event.getCommission())
                        .commissionDate(event.getCreatedAt())
                        .build())
                .toList())
            .build();

        producer.send(properties.getExchange(), properties.getRoutingKey(), message);

        record.getValue().forEach(commissionMessage -> {
          commissionMessage.setStatus(CommissionOutboxStatus.SENT);
          commissionMessage.setSentAt(ZonedDateTime.now());
        });

      } catch (Exception e) {
        log.error("Failed to send commission message for user  {}: {}", record.getKey(),
            e.getMessage());
        record.getValue().forEach(commissionMessage ->
            commissionMessage.setStatus(CommissionOutboxStatus.FAILED));
      }

      outboxRepository.saveAll(record.getValue());
    }
  }


  @Override
  public void processCallbackMessages(CommissionMessage message) {
    List<BigInteger> orderIds = message.getOrders().stream().map(Order::getOrderId).toList();

    if (message.getStatus().equals(Constant.SUCCESS)) {
      List<ShopeeOrder> shopeeOrders = orderRepository.findShopeeOrderByIdIn(orderIds);
      shopeeOrders.forEach(shopeeOrder -> shopeeOrder.setStatus(OrderStatus.COMMISSIONED));
      orderRepository.saveAll(shopeeOrders);
      return;
    }

    List<CommissionOutbox> events = outboxRepository.findByOrderIdIn(orderIds);

    events.forEach(event -> {
      event.setStatus(CommissionOutboxStatus.FAILED);
      event.setRetry(message.getRetry());
      event.setErrorMessage(event.getErrorMessage());
    });

    outboxRepository.saveAll(events);
  }

}

