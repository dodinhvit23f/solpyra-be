package com.shopizer.domain.user.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopizer.common.dto.response.PageObject;
import com.shopizer.configuration.RabbitQueuesProperties;
import com.shopizer.configuration.RabbitQueuesProperties.QueueProperties;
import com.shopizer.constant.ApplicationMessage.ErrorMessage;
import com.shopizer.constant.OrderStatus;
import com.shopizer.domain.user.dto.UserShopeeOrder;
import com.shopizer.domain.user.dto.UserShopeeProduct;
import com.shopizer.domain.user.dto.request.UserAddOrderRequest;
import com.shopizer.domain.user.dto.request.UserOrderListRequest;
import com.shopizer.domain.user.mapper.UserShopeeOrderMapper;
import com.shopizer.domain.user.repositories.ShopeeOrderRepository;
import com.shopizer.domain.user.repositories.UserRepository;
import com.shopizer.domain.user.repositories.UsersShopeeOrderRepository;
import com.shopizer.domain.user.repositories.impl.UsersShopeeOrderDSLRepository;
import com.shopizer.domain.user.services.UserShopeeOrderService;
import com.shopizer.entities.ShopeeOrder;
import com.shopizer.entities.UserPerShopeeOrder;
import com.shopizer.entities.Users;
import com.shopizer.exception.NotFoundException;
import com.shopizer.rabbitmq.MessageProducer;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserShopeeOrderServiceImpl implements UserShopeeOrderService {

  final UsersShopeeOrderRepository userShopeeOrderRepository;
  final UsersShopeeOrderDSLRepository userShopeeOrderDSLRepository;
  final UserRepository userRepository;
  final ShopeeOrderRepository shopeeOrderRepository;

  final MessageProducer producer;
  final RabbitQueuesProperties rabbitQueuesProperties;

  final UserShopeeOrderMapper userShopeeOrderMapper;
  final ObjectMapper objectMapper;

  @Override
  public PageObject getOrderList(UserOrderListRequest request) {

    Page<UserShopeeOrder> page = userShopeeOrderDSLRepository.findAll(request);

    return PageObject.builder()
        .page(request.getPageRequest().getPageNumber())
        .pageSize(request.getPageRequest().getPageSize())
        .totalPage(page.getTotalPages())
        .list(page.getContent())
        .build();
  }

  @Override
  public void sendAddOrderRequestToQueue(UserAddOrderRequest request) {

    try {
      QueueProperties queue = rabbitQueuesProperties.getAddOrder();
      producer.send(queue.getExchange(), queue.getRoutingKey(), request);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e.getMessage());
    }

  }

  @Override
  public void mapOrderForUser(UserAddOrderRequest request)
      throws BadRequestException, NotFoundException {
    Optional<UserPerShopeeOrder> userOrder = userShopeeOrderRepository.findByOrder_OrderId(
        request.getOrderCode());

    if (userOrder.isPresent()) {
      throw new BadRequestException(ErrorMessage.ORDER_CODE_EXIST);
    }

    Users user = userRepository.getByUserName(request.getUserName());
    Optional<ShopeeOrder> userShopeeOrder = shopeeOrderRepository.findByOrderId(
        request.getOrderCode());

    if (userShopeeOrder.isEmpty() ||
        userShopeeOrder.get().getUserCommission().compareTo(BigDecimal.ONE) < 0) {
      throw new NotFoundException(ErrorMessage.ORDER_NOT_EXIST);
    }

    UserPerShopeeOrder order = UserPerShopeeOrder.builder()
        .user(user)
        .order(userShopeeOrder.get())
        .product(userShopeeOrder.get().getProduct())
        .createDate(ZonedDateTime.now())
        .paymentApproved(userShopeeOrder.get().getStatus().equals(OrderStatus.COMMISSIONED))
        .build();

    userShopeeOrderRepository.save(order);
  }

  @Override
  @Cacheable(value = "affiliate-link")
  public String getRandomAffiliateLink(){
      return userShopeeOrderRepository.findRandomOrder();
  }

  @Override
  public List<UserShopeeProduct> getTopProduct(int number, String name) {
    return userShopeeOrderRepository.getTopProduct(name, PageRequest.of(0, number));
  }
}
