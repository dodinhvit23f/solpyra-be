package com.solpyra.domain.admin.repositories;

import com.solpyra.entities.ShopeeProduct;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

public interface AdminShopeeProductRepository extends JpaRepository<ShopeeProduct, BigInteger>,
    QuerydslPredicateExecutor<ShopeeProduct> {

  @Query("""
      SELECT p
      FROM ShopeeProduct p
      WHERE p.productCode IN :productCodes
      """)
  List<ShopeeProduct> getListWithCollectionOfProductCodes(@Param("productCodes") Collection<String> productCodes);


  Optional<ShopeeProduct> findShopeeProductById(BigInteger id);
}
