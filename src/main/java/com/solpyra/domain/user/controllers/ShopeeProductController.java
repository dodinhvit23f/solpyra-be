package com.solpyra.domain.user.controllers;

import com.solpyra.common.dto.response.Response;
import com.solpyra.common.constant.Constant;
import com.solpyra.common.constant.OrderStatus;
import com.solpyra.domain.user.services.UserShopeeOrderService;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/product/v1")
@RequiredArgsConstructor
public class ShopeeProductController {

  final UserShopeeOrderService userShopeeOrderService;

  @GetMapping("/shopee/affiliate/link")
  ResponseEntity<Response<String>> getRandomAffiliateLink() {
    return ResponseEntity.ok(Response.<String>builder()
        .data(userShopeeOrderService.getRandomAffiliateLink())
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }


}
