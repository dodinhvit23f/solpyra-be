package com.shopizer.domain.user.controllers;

import com.shopizer.common.dto.response.PageObject;
import com.shopizer.common.dto.response.Response;
import com.shopizer.constant.Constant;
import com.shopizer.constant.OrderStatus;
import com.shopizer.domain.user.dto.request.UserAddOrderRequest;
import com.shopizer.domain.user.dto.request.UserOrderListRequest;
import com.shopizer.domain.user.services.UserShopeeOrderService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.Set;
import lombok.RequiredArgsConstructor;
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
