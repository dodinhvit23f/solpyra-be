package com.shopizer.domain.authentication.services;

import com.shopizer.domain.authentication.dto.request.ChangePasswordRequest;
import com.shopizer.domain.authentication.dto.request.OTPGenerateRequest;
import com.shopizer.domain.authentication.dto.request.SignInRequest;
import com.shopizer.domain.authentication.dto.response.SignInResponse;
import com.shopizer.domain.authentication.dto.response.TokenAuthenticationResponse;
import java.io.ByteArrayOutputStream;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

public interface AuthenticationService {

  SignInResponse signIn(SignInRequest signInRequest) throws BadRequestException;

  SignInResponse signInWithMFA(String authenticationToken,
      String otpCode) throws BadRequestException;

  TokenAuthenticationResponse refreshToken(String refreshToken) throws BadRequestException;

  @Transactional
  void changeUserPassword(String authenticationToken, ChangePasswordRequest changePassword)
      throws BadRequestException;

  @Transactional
  ByteArrayOutputStream generateOTP(String authenticationToken,
      OTPGenerateRequest otpGenerate);

  @Transactional
  void verifyOtp(String authenticationToken, String otpCode) throws BadRequestException;
}
