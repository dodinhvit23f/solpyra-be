package com.solpyra.domain.admin.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.solpyra.common.dto.response.PageObject;
import com.solpyra.domain.admin.dto.request.AdminOrderListRequest;
import com.solpyra.domain.admin.mapper.AdminShopeeOrderMapper;
import com.solpyra.domain.admin.repositories.AdminShopeeOrderRepository;
import com.solpyra.domain.admin.services.AdminShopeeOrderService;
import com.solpyra.entities.QShopeeOrder;
import com.solpyra.entities.ShopeeOrder;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminShopeeOrderServiceImpl implements AdminShopeeOrderService {

  final JPAQueryFactory jpaQueryFactory;
  final AdminShopeeOrderRepository adminShopeeOrderRepository;
  final AdminShopeeOrderMapper shopeeOrderMapper;

  @Override
  public PageObject getOrderList(AdminOrderListRequest request) {
    Pageable pageRequest = request.getPageRequest();
    QShopeeOrder shopeeOrder = QShopeeOrder.shopeeOrder;

    BooleanExpression searchCondition = shopeeOrder.id.isNotNull();
    Page<ShopeeOrder> pageOrder = null;

    if (!ObjectUtils.isEmpty(request.getSearch())) {
      String searchValue = request.getSearch();
      searchCondition = searchCondition.and(shopeeOrder.orderId.eq(searchValue)
          .or(shopeeOrder.product.productName.containsIgnoreCase(searchValue)
              .or(shopeeOrder.product.storeName.containsIgnoreCase(searchValue))));
    }

    if (Objects.nonNull(request.getStatuses())) {
      searchCondition = searchCondition.and(shopeeOrder.status.in(request.getStatuses()));
    }

    if (Objects.isNull(searchCondition)) {
      pageOrder = adminShopeeOrderRepository.findAll(pageRequest);
    }

    if (Objects.nonNull(searchCondition)) {
      pageOrder = adminShopeeOrderRepository.findAll(searchCondition, pageRequest);
    }


    return PageObject.builder()
        .page(pageRequest.getPageNumber())
        .pageSize(pageRequest.getPageSize())
        .totalPage(pageOrder.getTotalPages())
        .list(pageOrder.getContent()
            .stream()
            .map(shopeeOrderMapper::toAdminShopeeOrderDto)
            .toList())
        .build();
  }
}
