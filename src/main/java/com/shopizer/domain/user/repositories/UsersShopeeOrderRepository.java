package com.shopizer.domain.user.repositories;

import com.querydsl.core.types.Predicate;
import com.shopizer.entities.QUserPerShopeeOrder;
import com.shopizer.entities.ShopeeOrder;
import com.shopizer.entities.UserPerShopeeOrder;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;

public interface UsersShopeeOrderRepository extends JpaRepository<UserPerShopeeOrder, BigInteger>,
    QuerydslPredicateExecutor<UserPerShopeeOrder>{

  @EntityGraph(attributePaths = {"user", "product", "order"})
  Page<UserPerShopeeOrder> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"user", "product", "order"})
  Page<UserPerShopeeOrder> findAll(Predicate predicate,Pageable pageable);

  Optional<UserPerShopeeOrder> findByOrder_OrderId(String orderOrderId);
}
