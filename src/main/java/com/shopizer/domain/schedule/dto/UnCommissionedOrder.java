package com.shopizer.domain.schedule.dto;

import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnCommissionedOrder {
  BigInteger orderId;
  BigInteger userId;
}
