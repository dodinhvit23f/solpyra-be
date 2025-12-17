package com.solpyra.domain.authentication.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignInWithOTPRequest {

  private String otp;
}
