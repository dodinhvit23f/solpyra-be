package com.solpyra.domain.admin.services;

import com.solpyra.common.dto.response.PageObject;
import com.solpyra.domain.admin.dto.request.AdminOrderListRequest;

public interface AdminShopeeOrderService {

  PageObject getOrderList(AdminOrderListRequest request);
}
