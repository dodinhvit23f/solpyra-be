package com.solpyra.domain.admin.controllers;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.common.dto.response.Response;
import com.solpyra.common.constant.Constant;
import com.solpyra.domain.admin.dto.request.AdminProductListRequest;
import com.solpyra.domain.admin.dto.request.AdminUpdateAffiliateLinkRequest;
import com.solpyra.domain.admin.services.AdminShopeeProductService;
import com.solpyra.exception.NotFoundException;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shopee/products/v1")
public class AdminShopeeProductController {

  final AdminShopeeProductService adminShopeeProductService;

  @GetMapping("/list")
  ResponseEntity<Response<PageObject>> findAllProducts(@RequestParam(value = "isAffiliate", required = false) Boolean isAffiliate,
      @RequestParam(value = "search", required = false) String search,
      Pageable pageable) {

    return ResponseEntity.ok(Response.<PageObject>builder()
        .data(adminShopeeProductService.getProducts(AdminProductListRequest.builder()
                .isNullAffiliate(isAffiliate)
                .search(search)
                .pageable(pageable)
            .build()))
        .traceId(MDC.get(Constant.TRACE_ID))
        .build());
  }

  @PutMapping("/{id}/affiliate-link")
  ResponseEntity<Object> updateAffiliateLink(@PathVariable("id") BigInteger id,
      @RequestBody AdminUpdateAffiliateLinkRequest request) throws NotFoundException {

    request.setId(id);
    adminShopeeProductService.updateAffiliateLink(request);

    return ResponseEntity.ok(Response.<PageObject>builder()
            .traceId(MDC.get(Constant.TRACE_ID))
            .build());
  }

}
