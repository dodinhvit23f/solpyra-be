package com.solpyra.domain.authentication.dto.request;

import lombok.Data;

@Data
public class GoogleSsoSignInRequest {

  String idToken;
  String email;
}
