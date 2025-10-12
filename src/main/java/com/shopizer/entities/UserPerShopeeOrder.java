package com.shopizer.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Table(name = "user_shopee_order")
public class UserPerShopeeOrder extends BaseEntity {

  @Column(name = "user_id", insertable = false, updatable = false)
  BigInteger userId;
  @Column(name = "order_id", insertable = false, updatable = false)
  BigInteger orderId;
  @Column(name = "product_id", insertable = false, updatable = false)
  BigInteger productId;

  @Column(name = "create_date")
  ZonedDateTime createDate;

  @Column(name = "payment_approved")
  boolean paymentApproved;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private Users user;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private ShopeeProduct product;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private ShopeeOrder order;
}
