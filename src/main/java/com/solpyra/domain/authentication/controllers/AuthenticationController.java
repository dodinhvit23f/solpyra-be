package com.solpyra.domain.authentication.controllers;

import com.solpyra.common.dto.response.Response;
import com.solpyra.common.constant.Constant;
import com.solpyra.domain.authentication.dto.request.SignInRequest;
import com.solpyra.domain.authentication.dto.request.SignInWithOTPRequest;
import com.solpyra.domain.authentication.dto.response.SignInResponse;
import com.solpyra.domain.authentication.services.AuthenticationService;
import com.solpyra.exception.NotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.MDC;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication/v1")
public class AuthenticationController {

  final AuthenticationService authenticationService;

  @PostMapping("/login")
  ResponseEntity<Response<SignInResponse>> login(@Valid @RequestBody SignInRequest signInRequest)
      throws NotFoundException {
    return ResponseEntity.ok(Response.<SignInResponse>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(authenticationService.signIn(signInRequest))
        .build());
  }

  @GetMapping("/verify/token")
  ResponseEntity<Response<Object>> verifyToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){
    return ResponseEntity
        .ok(Response.builder()
            .traceId(MDC.get(Constant.TRACE_ID))
            .data(accessToken)
            .build());
  }

  @PostMapping("/otp/generate")
  ResponseEntity<InputStreamResource> generateNewQrForOTP() {
    return null;
  }

  @PostMapping("/otp/login")
  ResponseEntity<Response<SignInResponse>> loginWithOTP(@RequestHeader(HttpHeaders.AUTHORIZATION) String otpToken,
      @RequestBody SignInWithOTPRequest signInWithOTPRequest)
      throws BadRequestException, NotFoundException {
    return ResponseEntity.ok(Response.<SignInResponse>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(authenticationService.signInWithMFA(otpToken, signInWithOTPRequest.getOtp()))
        .build());
  }

  @PostMapping("/otp/verify")
  ResponseEntity<?> verifyScannedOTP() {
    return null;
  }

  @PostMapping("/refresh")
  ResponseEntity<Response<SignInResponse>> getNewToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String refreshToken)
      throws BadRequestException, NotFoundException {
    return ResponseEntity.ok(Response.<SignInResponse>builder()
        .traceId(MDC.get(Constant.TRACE_ID))
        .data(authenticationService.refreshToken(refreshToken))
        .build());
  }

}
