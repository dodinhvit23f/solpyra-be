package com.solpyra.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solpyra.common.constant.Constant;
import com.solpyra.common.constant.OrderStatus;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShopeeOrder {
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
  private BigDecimal userCommission;
  private BigDecimal userCommissionRate;
  private OrderStatus status;
  private String affiliateLink;
}
