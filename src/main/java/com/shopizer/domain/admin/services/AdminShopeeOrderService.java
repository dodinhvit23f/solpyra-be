package com.shopizer.domain.admin.services;

import com.shopizer.common.dto.response.PageObject;
import com.shopizer.domain.admin.dto.request.AdminOrderListRequest;

public interface AdminShopeeOrderService {

  PageObject getOrderList(AdminOrderListRequest request);
}
