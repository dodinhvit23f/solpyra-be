package com.shopizer.domain.user.repositories.impl;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopizer.domain.user.dto.UserShopeeOrder;
import com.shopizer.domain.user.dto.request.UserOrderListRequest;
import com.shopizer.entities.QShopeeOrder;
import com.shopizer.entities.QShopeeProduct;
import com.shopizer.entities.QUserPerShopeeOrder;
import com.shopizer.entities.UserPerShopeeOrder;
import com.shopizer.util.Utils;
import java.security.Principal;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

@Repository
@RequiredArgsConstructor
public class UsersShopeeOrderDSLRepository {

  public static final String USER_PER_SHOPEE_ORDER = "userPerShopeeOrder";
  final JPAQueryFactory queryFactory;

  public PageImpl<UserShopeeOrder> findAll(UserOrderListRequest request) {
    Pageable pageRequest = request.getPageRequest();
    Principal principal = request.getPrincipal();

    QUserPerShopeeOrder userOrder = QUserPerShopeeOrder.userPerShopeeOrder;
    QShopeeProduct product = QShopeeProduct.shopeeProduct;
    QShopeeOrder order = QShopeeOrder.shopeeOrder;

    BooleanExpression searchCondition = userOrder.user.userName.eq(principal.getName());

    if (!ObjectUtils.isEmpty(request.getSearch())) {
      String searchValue = request.getSearch();
      searchCondition = searchCondition.and(userOrder.order.orderId.eq(searchValue)
          .or(userOrder.product.productName.containsIgnoreCase(searchValue)
              .or(userOrder.product.storeName.containsIgnoreCase(searchValue))));
    }

    if (Objects.nonNull(request.getStatuses())) {
      searchCondition = searchCondition.and(userOrder.order.status.in(request.getStatuses()));
    }

    List<UserShopeeOrder> userOrders = queryFactory
        .select(Projections.constructor(
            UserShopeeOrder.class,
            userOrder.id,
            product.productName,
            order.orderId,
            product.storeName,
            order.orderDate,
            order.completedDate,
            order.commissionedDate,
            order.userCommission,
            order.userCommissionRate,
            order.status,
            userOrder.paymentApproved
        ))
        .from(userOrder)
        .join(userOrder.product, product)
        .join(userOrder.order, order)
        .where(searchCondition)
        .orderBy(Utils.toOrderSpecifiers(pageRequest,
                new PathBuilder<>(UserPerShopeeOrder.class, USER_PER_SHOPEE_ORDER))
            .toArray(OrderSpecifier[]::new))
        .offset(pageRequest.getOffset())
        .limit(pageRequest.getPageSize())
        .fetch();

    Long total = queryFactory
        .select(userOrder.count())
        .from(userOrder)
        .leftJoin(userOrder.product, product)
        .leftJoin(userOrder.order, order)
        .where(searchCondition)
        .fetchOne();

    return new PageImpl<>(userOrders, pageRequest, total == null ? 0 : total);
  }
}
