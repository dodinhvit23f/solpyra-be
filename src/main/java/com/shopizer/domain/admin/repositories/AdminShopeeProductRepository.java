package com.shopizer.domain.admin.repositories;

import com.shopizer.entities.ShopeeOrder;
import com.shopizer.entities.ShopeeProduct;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface AdminShopeeProductRepository extends JpaRepository<ShopeeProduct, BigInteger>,
    QuerydslPredicateExecutor<ShopeeOrder> {

  @Query("""
      SELECT p
      FROM ShopeeProduct p
      WHERE p.productCode IN :productCodes
      """)
  List<ShopeeProduct> getListWithCollectionOfProductCodes(@Param("productCodes") Collection<String> productCodes);

}
