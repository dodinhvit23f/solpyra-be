package com.shopizer.domain.authentication.services.impl;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shopizer.constant.ApplicationMessage;
import com.shopizer.constant.Constant;
import com.shopizer.domain.authentication.dto.request.ChangePasswordRequest;
import com.shopizer.domain.authentication.dto.request.OTPGenerateRequest;
import com.shopizer.domain.authentication.dto.request.SignInRequest;
import com.shopizer.domain.authentication.dto.response.SignInResponse;
import com.shopizer.domain.authentication.dto.response.TokenAuthenticationResponse;
import com.shopizer.domain.authentication.repositories.AuthenticationRepository;
import com.shopizer.domain.authentication.services.AuthenticationService;
import com.shopizer.domain.authentication.services.JwtService;
import com.shopizer.entities.Role;
import com.shopizer.entities.Users;
import com.shopizer.exception.NotFoundException;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base32;
import org.apache.coyote.BadRequestException;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

  final AuthenticationRepository authenticationRepository;
  final JwtService jwtService;
  final PasswordEncoder passwordEncoder;
  final TimeBasedOneTimePasswordGenerator timeBasedOneTimePasswordGenerator;

  @Value("${application.jwt.end-after}")
  int endAfter;
  @Value("${application.jwt.extra-time}")
  int extraTime;
  @Value("${spring.application.name}")
  String appName;

  @Override
  public SignInResponse signIn(SignInRequest signInRequest) throws NotFoundException {

    Users user = authenticationRepository.findByUserNameAndIsDeleted(signInRequest.getUsername(),
            Boolean.FALSE)
        .orElseThrow(() -> new NotFoundException(
            ApplicationMessage.AuthenticationMessage.USER_OR_PASSWORD_NOT_EXIST));

    if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
      log.error("Trace id {} login with not exist user {}",
          MDC.get(Constant.TRACE_ID), signInRequest.getUsername());
      throw new NotFoundException(
          ApplicationMessage.AuthenticationMessage.USER_OR_PASSWORD_NOT_EXIST);
    }

    if (!user.isHaveMfa() && user.getRole().getName().equals(Constant.USER)) {
      return createJwtToken(user);
    }

    User userDetail = new User(user.getUserName(),
        passwordEncoder.encode(signInRequest.getPassword()),
        List.of(new SimpleGrantedAuthority(Constant.UN_VALIDATE)));
    String token = jwtService.generateToken(userDetail, UUID.randomUUID().toString(), 3,
        Constant.ACCESS);

    return SignInResponse.builder()
        .haveMFA(user.isHaveMfa())
        .otpToken(token)
        .build();
  }

  @Override
  public SignInResponse signInWithMFA(String authenticationToken, String otpCode)
      throws BadRequestException, NotFoundException {

    User userFromToken = jwtService.getUserFromJwtToken(authenticationToken).orElseThrow(
        () -> new NotFoundException(ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));
    Users users = authenticationRepository.findByUserName(userFromToken.getUsername()).orElseThrow(
        () -> new NotFoundException(ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

    if (isNotRightOtp(users.getOtpSecret(), otpCode)) {
      throw new BadRequestException(ApplicationMessage.AuthenticationMessage.OTP_NOT_CORRECT);
    }

    return createJwtToken(users);
  }

  @Override
  public SignInResponse refreshToken(String refreshToken)
      throws BadRequestException, NotFoundException {

    if(!jwtService.validateRefreshJwtToken(refreshToken)){
        throw new  AuthenticationCredentialsNotFoundException(ApplicationMessage.AuthenticationMessage.INVAlID_TOKEN);
    }

    User userRefreshToken = jwtService.getUserFromJwtToken(refreshToken).orElseThrow(
        () -> new NotFoundException(ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

    Users users = authenticationRepository.findByUserName(userRefreshToken.getUsername())
        .orElseThrow(
            () -> new NotFoundException(ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

    if (ObjectUtils.isEmpty(users.getRole())) {
      throw new BadRequestException(ApplicationMessage.AuthenticationMessage.USER_NOT_ACTIVE);
    }

    return createJwtToken(users);
  }

  @Override
  public void changeUserPassword(String authenticationToken, ChangePasswordRequest changePassword)
      throws BadRequestException, NotFoundException {
    String username = jwtService.getUsernameFromJwtToken(authenticationToken)
        .orElseThrow(() -> new NotFoundException(
            ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

    Users user = authenticationRepository.findByUserName(username)
        .orElseThrow(() -> new NotFoundException(
            ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

    if (user.isSSOUser() && ObjectUtils.isEmpty(user.getPassword())) {
      user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
      authenticationRepository.save(user);
      return;
    }

    if (!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
      throw new BadRequestException(ApplicationMessage.AuthenticationMessage.PASSWORD_NOT_CORRECT);
    }

    user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
    authenticationRepository.save(user);
  }

  @Override
  public ByteArrayOutputStream generateOTP(String authenticationToken,
      OTPGenerateRequest otpGenerate) {
    try {
      User user = jwtService.getUserFromJwtToken(authenticationToken)
          .orElseThrow(() -> new NotFoundException(
              ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

      Users users = authenticationRepository.findByUserName(user.getUsername())
          .orElseThrow(() -> new NotFoundException(
              ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

      if (users.isHaveMfa() && isNotRightOtp(users.getOtpSecret(), otpGenerate.getOldOTP())) {
        if (ObjectUtils.isEmpty(otpGenerate.getOldOTP())) {
          throw new BadRequestException(ApplicationMessage.AuthenticationMessage.OTP_NOT_CORRECT);
        }
      }

      KeyGenerator keyGenerator = KeyGenerator.getInstance(
          timeBasedOneTimePasswordGenerator.getAlgorithm());
      int macLengthInBytes = Mac.getInstance(timeBasedOneTimePasswordGenerator.getAlgorithm())
          .getMacLength();
      keyGenerator.init(macLengthInBytes * 8);
      SecretKey key = keyGenerator.generateKey();
      Base32 base32 = new Base32();
      users.setOtpSecret(base32.encodeAsString(key.getEncoded()));
      QRCodeWriter barcodeWriter = new QRCodeWriter();
      BitMatrix bitMatrix =
          barcodeWriter.encode(generateQRUrl(users), BarcodeFormat.QR_CODE, 200, 200);

      authenticationRepository.save(users);
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      MatrixToImageWriter.writeToStream(bitMatrix, "PNG", stream);
      return stream;
    } catch (Exception e) {
      log.error(e.getMessage());
      throw new InternalError();
    }
  }

  @Override
  public void verifyOtp(String authenticationToken, String otpCode)
      throws NotFoundException, BadRequestException {
    User user = jwtService.getUserFromJwtToken(authenticationToken)
        .orElseThrow(() -> new NotFoundException(
            ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));
    Users users = authenticationRepository.findByUserName(user.getUsername())
        .orElseThrow(() -> new NotFoundException(
            ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

    if (!users.isHaveMfa()) {
      if (isNotRightOtp(users.getOtpSecret(), otpCode)) {
        throw new BadRequestException(ApplicationMessage.AuthenticationMessage.OTP_NOT_CORRECT);
      }

      users.setHaveMfa(Boolean.TRUE);
      authenticationRepository.save(users);
    }
  }

  private SignInResponse createJwtToken(Users user) throws NotFoundException {

    if (ObjectUtils.isEmpty(user.getRole())) {
      throw new NotFoundException(ApplicationMessage.AuthenticationMessage.USER_NOT_ACTIVE);
    }

    List<SimpleGrantedAuthority> roles = Stream.of(user.getRole())
        .map(Role::getName)
        .map(SimpleGrantedAuthority::new)
        .toList();

    String uuid = UUID.randomUUID().toString();
    User loginUser = new User(user.getUserName(), uuid, roles);

    return SignInResponse.builder()
        .accessToken(jwtService.generateToken(loginUser, uuid, endAfter, Constant.ACCESS))
        .refreshToken(
            jwtService.generateToken(loginUser, uuid, endAfter + extraTime, Constant.REFRESH))
        .roles(roles.stream().map(SimpleGrantedAuthority::getAuthority).toList())
        .haveMFA(user.isHaveMfa())
        .build();
  }

  private boolean isNotRightOtp(String otpSecret, String otpCode) {
    Base32 base32 = new Base32();
    byte[] key = base32.decode(otpSecret);
    SecretKey originalKey = new SecretKeySpec(key, 0, key.length,
        timeBasedOneTimePasswordGenerator.getAlgorithm());
    String otpGenerate;
    try {
      otpGenerate = timeBasedOneTimePasswordGenerator.generateOneTimePasswordString(originalKey,
          Instant.now());
    } catch (InvalidKeyException e) {
      throw new RuntimeException(e);
    }
    return !otpGenerate.equals(otpCode);
  }

  private String generateQRUrl(Users user) {
    return String.format(
        "otpauth://totp/%s:%s?secret=%s&issuer=%s",
        appName, user.getEmail(), user.getOtpSecret(), appName);
  }
}
