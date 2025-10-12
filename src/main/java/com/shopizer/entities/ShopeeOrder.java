package com.shopizer.entities;

import com.shopizer.constant.OrderStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@Table(name = "shopee_order")
@EqualsAndHashCode(callSuper = true)
@SuperBuilder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class ShopeeOrder extends LogEntity {

  @Column(name = "order_id")
  private String orderId;

  @Column(name = "status")
  @Enumerated(EnumType.ORDINAL)
  private OrderStatus status;

  @Column(name = "order_date")
  private ZonedDateTime orderDate;

  @Column(name = "completed_date")
  private ZonedDateTime completedDate;

  @Column(name = "commissioned_date")
  private ZonedDateTime commissionedDate;

  @Column(name = "total_commission")
  private BigDecimal totalCommission;

  @Column(name = "user_commission")
  private BigDecimal userCommission;

  @Column(name = "platform_commission")
  private BigDecimal platformCommission;

  @Column(name = "commission_rate")
  private BigDecimal commissionRate;

  @Column(name = "user_commission_rate")
  private BigDecimal userCommissionRate;

  @Column(name = "platform_commission_rate")
  private BigDecimal platformCommissionRate;

  @Column(name = "product_id", insertable = false, updatable = false)
  private BigInteger productId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private ShopeeProduct product;
}
