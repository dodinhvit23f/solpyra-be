package com.shopizer.domain.user.controllers;

import com.shopizer.common.dto.response.PageObject;
import com.shopizer.common.dto.response.Response;
import com.shopizer.constant.Constant;
import com.shopizer.constant.OrderStatus;
import com.shopizer.domain.user.dto.request.UserAddOrderRequest;
import com.shopizer.domain.user.dto.request.UserOrderListRequest;
import com.shopizer.domain.user.services.UserShopeeOrderService;
import com.shopizer.exception.NotFoundException;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shopee/orders/v1")
@RequiredArgsConstructor
public class UserShopeeOrderController {

  final UserShopeeOrderService userShopeeOrderService;

  @GetMapping("/list")
  ResponseEntity<Response<PageObject>> findAllOrders(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Set<OrderStatus> statuses,
      Pageable pageable, Principal principal) {

    return ResponseEntity.ok(Response.<PageObject>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(userShopeeOrderService.getOrderList(UserOrderListRequest.builder()
            .search(search)
            .statuses(statuses)
            .pageRequest(pageable)
            .principal(principal)
            .build()))
        .build());
  }

  @PostMapping("/add")
  ResponseEntity<Response> addOrder(@Valid @RequestBody UserAddOrderRequest request,
      Principal principal) {

    request.setUserName(principal.getName());
    userShopeeOrderService.sendAddOrderRequestToQueue(request);

    return ResponseEntity.ok(Response.builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }

}
