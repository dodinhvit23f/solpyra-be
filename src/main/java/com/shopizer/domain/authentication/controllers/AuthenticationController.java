package com.shopizer.domain.authentication.controllers;

import com.shopizer.common.dto.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/authentication/v1")
public class AuthenticationController {

  @PostMapping("/login")
  ResponseEntity<Response<?>> verifyAdmin() {
    return null;
  }

  @GetMapping("/verify/token")
  ResponseEntity<Response<?>> verifyTokenAdmin() {
    return null;
  }

  @PostMapping("/otp/generate")
  ResponseEntity<InputStreamResource> generateNewQrForOTP() {
    return null;
  }

  @PostMapping("/otp/login")
  ResponseEntity<Response<?>> loginWithOTP() {
    return null;
  }

  @PostMapping("/otp/verify")
  ResponseEntity<?> verifyScannedOTP() {
    return null;
  }

  @PostMapping("/refresh")
  ResponseEntity<?> getNewToken() {
    return null;
  }

}
