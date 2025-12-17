package com.solpyra.domain.admin.services;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.domain.admin.dto.request.AdminProductListRequest;
import com.solpyra.domain.admin.dto.request.AdminUpdateAffiliateLinkRequest;
import com.solpyra.exception.NotFoundException;
import org.springframework.transaction.annotation.Transactional;

public interface AdminShopeeProductService {

  @Transactional
  void updateAffiliateLink(AdminUpdateAffiliateLinkRequest request)
      throws NotFoundException;

  PageObject getProducts(AdminProductListRequest request);
}
