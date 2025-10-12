package com.shopizer.domain.authentication.controllers;

import com.shopizer.common.dto.response.Response;
import com.shopizer.constant.Constant;
import com.shopizer.domain.authentication.dto.request.GoogleSsoSignInRequest;
import com.shopizer.domain.authentication.dto.response.SignInResponse;
import com.shopizer.domain.authentication.services.SsoAuthenticationService;
import com.shopizer.exception.NotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication/v1/sso")
public class SsoAuthenticationController {

  final SsoAuthenticationService ssoAuthenticationService;

  @PostMapping("/google")
  public ResponseEntity<Response<SignInResponse>> google(
      @RequestBody GoogleSsoSignInRequest request)
      throws GeneralSecurityException, NotFoundException, IOException {

    return ResponseEntity.ok(Response.<SignInResponse>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(ssoAuthenticationService.verifyGoogleIdToken(request))
        .build());
  }
}
