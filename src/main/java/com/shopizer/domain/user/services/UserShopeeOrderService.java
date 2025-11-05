package com.shopizer.domain.user.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.shopizer.common.dto.response.PageObject;
import com.shopizer.domain.user.dto.UserShopeeProduct;
import com.shopizer.domain.user.dto.request.UserAddOrderRequest;
import com.shopizer.domain.user.dto.request.UserOrderListRequest;
import com.shopizer.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.List;
import org.apache.coyote.BadRequestException;

public interface UserShopeeOrderService {

  PageObject getOrderList(UserOrderListRequest request);

  void sendAddOrderRequestToQueue(@Valid UserAddOrderRequest request);

  void mapOrderForUser(UserAddOrderRequest request)
      throws BadRequestException, NotFoundException;

  String getRandomAffiliateLink();

  List<UserShopeeProduct> getTopProduct(int number, String name);
}
