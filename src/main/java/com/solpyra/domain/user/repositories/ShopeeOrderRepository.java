package com.solpyra.domain.user.repositories;

import com.solpyra.entities.ShopeeOrder;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopeeOrderRepository extends JpaRepository<ShopeeOrder, Long> {

  @EntityGraph(attributePaths = {"product"})
  Optional<ShopeeOrder> findByOrderId(String orderId);
}
