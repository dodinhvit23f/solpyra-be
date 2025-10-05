package com.shopizer.domain.admin.repositories;

import com.querydsl.core.types.Predicate;
import com.shopizer.entities.ShopeeOrder;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface AdminShopeeOrderRepository extends JpaRepository<ShopeeOrder, BigInteger>,
    QuerydslPredicateExecutor<ShopeeOrder> {

    @Query("""
    SELECT s
    FROM ShopeeOrder s
    JOIN FETCH s.product
    WHERE s.orderId IN :orderIds
    """)
  List<ShopeeOrder> findListShopeeOrdersByOrders(@Param("orderIds") Collection<String> orderIds);

   @EntityGraph(attributePaths = "product")
   Page<ShopeeOrder> findAll(Pageable pageable);

  @EntityGraph(attributePaths = "product")
  Page<ShopeeOrder> findAll(Predicate predicate,Pageable pageable);
}
