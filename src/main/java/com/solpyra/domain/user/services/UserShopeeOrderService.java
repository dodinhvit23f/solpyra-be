package com.solpyra.domain.user.services;

import com.solpyra.domain.user.dto.UserShopeeProduct;
import com.solpyra.exception.NotFoundException;
import com.solpyra.common.dto.response.PageObject;
import com.solpyra.common.dtos.rabitmq.UserAddOrderRequest;
import com.solpyra.common.dtos.rabitmq.UserOrderListRequest;
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
