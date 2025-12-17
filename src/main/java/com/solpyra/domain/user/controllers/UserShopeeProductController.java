package com.solpyra.domain.user.controllers;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.common.dto.response.Response;
import com.solpyra.common.constant.Constant;
import com.solpyra.domain.user.services.UserShopeeOrderService;
import java.security.Principal;
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
