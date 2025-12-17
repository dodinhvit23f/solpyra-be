package com.solpyra.domain.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Pageable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminProductListRequest {

  Pageable pageable;
  Boolean isNullAffiliate;
  String search;

}
