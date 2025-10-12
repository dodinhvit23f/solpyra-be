package com.shopizer.domain.user.controllers;

import com.shopizer.common.dto.response.PageObject;
import com.shopizer.common.dto.response.Response;
import com.shopizer.constant.Constant;
import com.shopizer.domain.user.dto.UserShopeeProduct;
import com.shopizer.domain.user.services.UserShopeeOrderService;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user/shopee/products/v1")
@RequiredArgsConstructor
public class UserShopeeProductController {

  final UserShopeeOrderService userShopeeOrderService;

  @GetMapping("/top/{number}")
  ResponseEntity<Response<PageObject>> findTopProducts(@PathVariable int number, Principal principal) {
    return ResponseEntity.ok(Response.<PageObject>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(PageObject.builder()
            .list(userShopeeOrderService.getTopProduct(number, principal.getName()))
            .build())
        .build());
  }
}
