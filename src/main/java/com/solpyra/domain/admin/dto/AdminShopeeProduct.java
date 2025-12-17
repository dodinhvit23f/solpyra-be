package com.solpyra.domain.admin.dto;

import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminShopeeProduct {
  private BigInteger id;
  private String productName;
  private String storeName;
  private String affiliateLink;
  private BigDecimal commissionRate;
  private String storeId;
  private String productCode;
}
