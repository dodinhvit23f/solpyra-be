package com.shopizer.domain.admin.dto.request;

import java.math.BigInteger;
import lombok.Data;

@Data
public class AdminUpdateAffiliateLinkRequest {
  private String affiliateLink;
  private BigInteger id;
}
