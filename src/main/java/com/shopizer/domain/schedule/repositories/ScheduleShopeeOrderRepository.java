package com.shopizer.domain.schedule.repositories;

import com.shopizer.constant.OrderStatus;
import com.shopizer.entities.ShopeeOrder;
import com.shopizer.entities.UserPerShopeeOrder;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ScheduleShopeeOrderRepository extends JpaRepository<ShopeeOrder, BigInteger> {

  @Query("""
          SELECT upso
          FROM ShopeeOrder o
          JOIN UserPerShopeeOrder upso ON upso.orderId = o.id
          WHERE o.status = :status
            AND o.completedDate <= :cutoff
            AND o.userCommission > 0
      """)
  List<UserPerShopeeOrder> findEligibleOrders(OrderStatus status, ZonedDateTime cutoff);

  List<ShopeeOrder> findShopeeOrderByIdIn(List<BigInteger> orderIds);

}
