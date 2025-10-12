package com.shopizer.domain.user.repositories;

import com.querydsl.core.types.Predicate;
import com.shopizer.domain.user.dto.UserShopeeProduct;
import com.shopizer.entities.UserPerShopeeOrder;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface UsersShopeeOrderRepository extends JpaRepository<UserPerShopeeOrder, BigInteger>,
    QuerydslPredicateExecutor<UserPerShopeeOrder>{

  @EntityGraph(attributePaths = {"user", "product", "order"})
  Page<UserPerShopeeOrder> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"user", "product", "order"})
  Page<UserPerShopeeOrder> findAll(Predicate predicate,Pageable pageable);

  Optional<UserPerShopeeOrder> findByOrder_OrderId(String orderOrderId);

  @Query("""
        SELECT  new com.shopizer.domain.user.dto.UserShopeeProduct(
                  sp.id,
                  sp.productName,
                  sp.storeName,
                  sp.affiliateLink,
                  COUNT(uso.orderId))
        FROM UserPerShopeeOrder uso
        JOIN ShopeeOrder so ON so.id = uso.orderId
        JOIN ShopeeProduct sp ON sp.id = uso.productId
        JOIN Users u ON u.id = uso.userId
        WHERE u.userName = :principalName
        GROUP BY sp.id, sp.productName, sp.storeName, sp.affiliateLink
        ORDER BY COUNT(uso.orderId) DESC
        """)
  List<UserShopeeProduct> getTopProduct(@Param("principalName") String principalName, Pageable pageable);
}
