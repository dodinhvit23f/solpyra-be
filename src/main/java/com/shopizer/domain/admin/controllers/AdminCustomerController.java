package com.shopizer.domain.admin.controllers;

import com.shopizer.common.dto.response.Response;
import com.shopizer.constant.Constant;
import com.shopizer.domain.admin.services.AdminCustomerService;
import java.math.BigInteger;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/customers/v1")
public class AdminCustomerController {

  final AdminCustomerService adminCustomerService;

  @GetMapping("/name/list")
  public ResponseEntity<Response<Map<BigInteger, String>>> findAllCustomersName() {

    return ResponseEntity.ok(Response.<Map<BigInteger, String>>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(adminCustomerService.findAllCustomersName())
        .build());
  }

}
