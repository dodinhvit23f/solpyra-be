package com.solpyra.domain.authentication.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignInResponse extends TokenAuthenticationResponse {

  private boolean haveMFA;
  private String otpToken;

}
