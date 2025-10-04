package com.shopizer.domain.admin.controllers;

import com.shopizer.common.dto.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shopee/orders/v1")
public class AdminShopeeOrderController {

  @PostMapping("/import")
  ResponseEntity<Response<?>> importFileOrders() {
    return null;
  }

  @PostMapping("/list")
  ResponseEntity<Response<?>> findAllOrders() {
    return null;
  }

}
