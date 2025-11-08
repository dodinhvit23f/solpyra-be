package com.shopizer.domain.user.dto.request;


import com.shopizer.constant.ApplicationMessage.ErrorMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UserAddOrderRequest {

  @NotEmpty(message = ErrorMessage.ORDER_CODE_NOT_EMPTY)
  String orderCode;
  String userName;
}
