package com.shopizer.domain.admin.services.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopizer.common.dto.response.PageObject;
import com.shopizer.domain.admin.dto.request.AdminOrderListRequest;
import com.shopizer.domain.admin.mapper.ShopeeOrderMapper;
import com.shopizer.domain.admin.repositories.AdminShopeeOrderRepository;
import com.shopizer.domain.admin.services.AdminShopeeOrderService;
import com.shopizer.entities.QShopeeOrder;
import com.shopizer.entities.ShopeeOrder;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminShopeeOrderServiceImpl implements AdminShopeeOrderService {

  final JPAQueryFactory jpaQueryFactory;
  final AdminShopeeOrderRepository adminShopeeOrderRepository;
  final ShopeeOrderMapper shopeeOrderMapper;

  @Override
  public PageObject getOrderList(AdminOrderListRequest request) {
    Pageable pageRequest = request.getPageRequest();
    QShopeeOrder shopeeOrder = QShopeeOrder.shopeeOrder;

    BooleanExpression searchCondition = shopeeOrder.id.isNotNull();
    Page<ShopeeOrder> pageOrder = null;

    if (!ObjectUtils.isEmpty(request.getSearch())) {
      searchCondition = searchCondition.and(shopeeOrder.orderId.eq(request.getSearch())
          .or(shopeeOrder.product.productName.contains(request.getSearch())
              .or(shopeeOrder.product.storeName.contains(request.getSearch()))));
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
