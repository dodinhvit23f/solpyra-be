package com.shopizer.domain.authentication.controllers;

import com.shopizer.common.dto.response.Response;
import com.shopizer.domain.authentication.dto.request.SignInRequest;
import com.shopizer.domain.authentication.dto.request.SignInWithOTPRequest;
import com.shopizer.domain.authentication.dto.response.SignInResponse;
import com.shopizer.domain.authentication.services.AuthenticationService;
import com.shopizer.exception.NotFoundException;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
        .data(authenticationService.signIn(signInRequest))
        .build());
  }

  @GetMapping("/verify/token")
  ResponseEntity<Response<Object>> verifyToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessToken){
    return ResponseEntity
        .ok(Response.builder()
            .traceId(UUID.randomUUID().toString())
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
        .data(authenticationService.refreshToken(refreshToken))
        .build());
  }

}
