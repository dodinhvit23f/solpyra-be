package com.shopizer.domain.authentication.services;

import com.shopizer.domain.authentication.dto.request.ChangePasswordRequest;
import com.shopizer.domain.authentication.dto.request.OTPGenerateRequest;
import com.shopizer.domain.authentication.dto.request.SignInRequest;
import com.shopizer.domain.authentication.dto.response.SignInResponse;
import com.shopizer.entities.Users;
import com.shopizer.exception.NotFoundException;
import java.io.ByteArrayOutputStream;
import org.apache.coyote.BadRequestException;
import org.springframework.transaction.annotation.Transactional;

public interface AuthenticationService {

  SignInResponse signIn(SignInRequest signInRequest)
      throws NotFoundException;

  SignInResponse signInWithMFA(String authenticationToken,
      String otpCode) throws NotFoundException, BadRequestException;

  SignInResponse refreshToken(String refreshToken)
      throws BadRequestException, NotFoundException;

  @Transactional
  void changeUserPassword(String authenticationToken, ChangePasswordRequest changePassword)
      throws BadRequestException, NotFoundException;

  SignInResponse createJwtToken(Users user, int accessTokenExpired, int refreshTokenExpired)
      throws NotFoundException;

  @Transactional
  Users saveSsoUser(String email);

  @Transactional
  ByteArrayOutputStream generateOTP(String authenticationToken,
      OTPGenerateRequest otpGenerate);

  @Transactional
  void verifyOtp(String authenticationToken, String otpCode)
      throws BadRequestException, NotFoundException;

}
