package com.shopizer.domain.admin.controllers;

import com.shopizer.common.dto.response.Response;
import java.math.BigInteger;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/shopee/products/v1")
public class AdminShopeeProductController {

  @PostMapping("/list")
  ResponseEntity<Response<?>> findAllProducts(PageRequest pageable) {
    return null;
  }

  @PutMapping("/{id}/affiliate-link")
  ResponseEntity<Response<?>> updateAffiliateLink(@PathVariable("id") BigInteger id) {
    return null;
  }

}
