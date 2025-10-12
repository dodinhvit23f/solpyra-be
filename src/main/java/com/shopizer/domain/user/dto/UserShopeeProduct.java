package com.shopizer.domain.user.dto;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserShopeeProduct {
  private BigInteger id;
  private String productName;
  private String storeName;
  private String affiliateLink;
  private Long times;
}
