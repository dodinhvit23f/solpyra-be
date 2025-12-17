package com.solpyra.domain.admin.controllers;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.common.dto.response.Response;
import com.solpyra.common.constant.Constant;
import com.solpyra.common.constant.OrderStatus;
import com.solpyra.domain.admin.dto.request.AdminOrderListRequest;
import com.solpyra.domain.admin.services.AdminShopeeOrderImportService;
import com.solpyra.domain.admin.services.AdminShopeeOrderService;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shopee/orders/v1")
public class AdminShopeeOrderController {

  final AdminShopeeOrderImportService adminShopeeOrderImportService;
  final AdminShopeeOrderService adminShopeeOrderService;

  @PostMapping("/import")
  ResponseEntity<Object> importFileOrders(@RequestParam("file") MultipartFile file) {
    adminShopeeOrderImportService.importShopeeOrderByCsvFile(file);

    return ResponseEntity.ok(Response.<PageObject>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }

  @GetMapping("/list")
  ResponseEntity<Response<PageObject>> findAllOrders(
      @RequestParam(required = false) String search,
      @RequestParam(required = false) Set<OrderStatus> statuses,
      Pageable pageable) {

    return ResponseEntity.ok(Response.<PageObject>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(adminShopeeOrderService.getOrderList(AdminOrderListRequest.builder()
            .search(search)
            .statuses(statuses)
            .pageRequest(pageable)
            .build()))
        .build());
  }

}
