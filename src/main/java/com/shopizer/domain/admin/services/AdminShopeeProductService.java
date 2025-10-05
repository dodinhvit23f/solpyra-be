package com.shopizer.domain.admin.services;

import com.shopizer.common.dto.response.PageObject;
import com.shopizer.domain.admin.dto.request.AdminProductListRequest;
import com.shopizer.domain.admin.dto.request.AdminUpdateAffiliateLinkRequest;
import com.shopizer.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface AdminShopeeProductService {

  @Transactional
  void updateAffiliateLink(AdminUpdateAffiliateLinkRequest request)
      throws NotFoundException;

  PageObject getProducts(AdminProductListRequest request);
}
