package com.shopizer.domain.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.shopizer.constant.Constant;
import com.shopizer.constant.OrderStatus;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminShopeeOrder {

  private BigInteger id;
  private String productName;
  private String orderId;
  private String storeName;
  @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, timezone = JsonFormat.DEFAULT_TIMEZONE)
  private ZonedDateTime orderDate;
  @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, timezone = JsonFormat.DEFAULT_TIMEZONE)
  private ZonedDateTime completedDate;
  @JsonFormat(pattern = Constant.DATE_TIME_FORMAT, timezone = JsonFormat.DEFAULT_TIMEZONE)
  private ZonedDateTime commissionedDate;
  private BigDecimal totalCommission;
  private BigDecimal userCommission;
  private BigDecimal platformCommission;
  private BigDecimal commissionRate;
  private BigDecimal userCommissionRate;
  private BigDecimal platformCommissionRate;
  private OrderStatus status;

}
