package com.shopizer.domain.authentication.dto.request;

import com.shopizer.constant.ApplicationMessage.ErrorMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    @NotEmpty(message = ErrorMessage.USERNAME_IS_EMPTY)
    String username;
    @NotEmpty(message = ErrorMessage.PASSWORD_IS_EMPTY)
      String password;
}
