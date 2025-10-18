package com.shopizer.domain.schedule.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommissionMessage {
  private String id;
  private BigInteger userId;
  private List<Order> orders;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Order {
    private BigInteger orderId;
    private BigDecimal commissionAmount;
  }
}