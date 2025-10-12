package com.shopizer.domain.user.dto.request;

import com.shopizer.constant.OrderStatus;
import java.security.Principal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserOrderListRequest {
  String search;
  Set<OrderStatus> statuses;
  Pageable pageRequest;
  Principal principal;
}
