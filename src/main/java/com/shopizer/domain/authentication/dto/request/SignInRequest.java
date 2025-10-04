package com.shopizer.domain.authentication.dto.request;

import com.shopizer.constant.ApplicationMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInRequest {
    @NotEmpty(message = ApplicationMessage.AuthenticationMessage.USERNAME_IS_EMPTY)
    String username;
    @NotEmpty(message = ApplicationMessage.AuthenticationMessage.PASSWORD_IS_EMPTY)
    String password;
}
