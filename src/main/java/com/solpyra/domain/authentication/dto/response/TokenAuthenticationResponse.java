package com.solpyra.domain.authentication.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class TokenAuthenticationResponse {
    String accessToken;
    String refreshToken;
    List<String> roles;
}
