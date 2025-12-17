package com.solpyra.domain.authentication.services.impl;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.solpyra.common.constant.ApplicationMessage.ErrorMessage;
import com.solpyra.common.constant.Constant;
import com.solpyra.domain.authentication.dto.request.ChangePasswordRequest;
import com.solpyra.domain.authentication.dto.request.OTPGenerateRequest;
import com.solpyra.domain.authentication.dto.request.SignInRequest;
import com.solpyra.domain.authentication.dto.response.SignInResponse;
import com.solpyra.domain.authentication.repositories.AuthenticationRepository;
import com.solpyra.domain.authentication.repositories.AuthenticationRoleRepository;
import com.solpyra.domain.authentication.services.AuthenticationService;
import com.solpyra.domain.authentication.services.JwtService;
import com.solpyra.entities.Role;
import com.solpyra.entities.Users;
import com.solpyra.exception.NotFoundException;
import com.solpyra.util.Utils;
import com.sun.jdi.InternalException;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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

  static final int OTP_TIMEOUT = 3;
  final AuthenticationRepository authenticationRepository;
  final AuthenticationRoleRepository authenticationRoleRepository;
  final JwtService jwtService;
  final PasswordEncoder passwordEncoder;
  final TimeBasedOneTimePasswordGenerator timeBasedOneTimePasswordGenerator;

  @Value("${application.jwt.end-after}")
  int accessTimeOut;
  @Value("${application.jwt.extra-time}")
  int refreshTimeOut;
  @Value("${spring.application.name}")
  String appName;

  @Override
  public SignInResponse signIn(SignInRequest signInRequest) throws NotFoundException {

    Users user = authenticationRepository.findByUserNameAndIsDeleted(signInRequest.getUsername(),
            Boolean.FALSE)
        .orElseThrow(() -> new NotFoundException(
            ErrorMessage.USER_OR_PASSWORD_NOT_EXIST));

    if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
      log.error("Trace id {} login with not exist user {}",
          MDC.get(Constant.TRACE_ID), signInRequest.getUsername());
      throw new NotFoundException(
          ErrorMessage.USER_OR_PASSWORD_NOT_EXIST);
    }

    if (!user.isHaveMfa() && user.getRole().getName().equals(Constant.USER)) {
      return createJwtToken(user, accessTimeOut, refreshTimeOut);
    }

    User userDetail = new User(user.getUserName(),
        MDC.get(Constant.TRACE_ID),
        List.of(new SimpleGrantedAuthority(Constant.UN_VALIDATE)));

    String token = jwtService.generateToken(userDetail,
        MDC.get(Constant.TRACE_ID), Utils.plusDate(new Date(), Calendar.MINUTE, OTP_TIMEOUT),
        Constant.ACCESS, user.getId());

    return SignInResponse.builder()
        .haveMFA(user.isHaveMfa())
        .otpToken(token)
        .build();
  }

  @Override
  public SignInResponse signInWithMFA(String authenticationToken, String otpCode)
      throws BadRequestException, NotFoundException {

    User userFromToken = jwtService.getUserFromJwtToken(authenticationToken).orElseThrow(
        () -> new NotFoundException(ErrorMessage.USER_NOT_EXIST));
    Users users = authenticationRepository.findByUserName(userFromToken.getUsername()).orElseThrow(
        () -> new NotFoundException(ErrorMessage.USER_NOT_EXIST));

    if (isNotRightOtp(users.getOtpSecret(), otpCode)) {
      throw new BadRequestException(ErrorMessage.OTP_NOT_CORRECT);
    }

    return createJwtToken(users, accessTimeOut, refreshTimeOut);
  }

  @Override
  public SignInResponse refreshToken(String refreshToken)
      throws BadRequestException, NotFoundException {

    if (!jwtService.validateRefreshJwtToken(refreshToken)) {
      throw new AuthenticationCredentialsNotFoundException(
          ErrorMessage.INVAlID_TOKEN);
    }

    User userRefreshToken = jwtService.getUserFromJwtToken(refreshToken).orElseThrow(
        () -> new NotFoundException(ErrorMessage.USER_NOT_EXIST));

    Users users = authenticationRepository.findByUserName(userRefreshToken.getUsername())
        .orElseThrow(
            () -> new NotFoundException(ErrorMessage.USER_NOT_EXIST));

    if (ObjectUtils.isEmpty(users.getRole())) {
      throw new BadRequestException(ErrorMessage.USER_NOT_ACTIVE);
    }

    return createJwtToken(users, accessTimeOut, refreshTimeOut);
  }

  @Override
  public void changeUserPassword(String authenticationToken, ChangePasswordRequest changePassword)
      throws BadRequestException, NotFoundException {
    String username = jwtService.getUsernameFromJwtToken(authenticationToken)
        .orElseThrow(() -> new NotFoundException(
            ErrorMessage.USER_NOT_EXIST));

    Users user = authenticationRepository.findByUserName(username)
        .orElseThrow(() -> new NotFoundException(
            ErrorMessage.USER_NOT_EXIST));

    if (user.isSSOUser() && ObjectUtils.isEmpty(user.getPassword())) {
      user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
      authenticationRepository.save(user);
      return;
    }

    if (!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
      throw new BadRequestException(ErrorMessage.PASSWORD_NOT_CORRECT);
    }

    user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
    authenticationRepository.save(user);
  }

  @Override
  public SignInResponse createJwtToken(Users user, int accessTokenExpired, int refreshTokenExpired)
      throws NotFoundException {
    Date now = new Date();
    Date accessTokenExpiredTime = Utils.plusDate(now, Calendar.DATE, accessTimeOut);
    Date refreshTokenExpiredTime = Utils.plusDate(now, Calendar.DATE, refreshTimeOut);

    if (ObjectUtils.isEmpty(user.getRole())) {
      throw new NotFoundException(ErrorMessage.USER_NOT_ACTIVE);
    }

    List<SimpleGrantedAuthority> roles = Stream.of(user.getRole())
        .map(Role::getName)
        .map(SimpleGrantedAuthority::new)
        .toList();

    String uuid = MDC.get(Constant.TRACE_ID);
    User loginUser = new User(user.getUserName(), uuid, roles);

    return SignInResponse.builder()
        .accessToken(jwtService.generateToken(loginUser, uuid, accessTokenExpiredTime, Constant.ACCESS, user.getId()))
        .refreshToken(jwtService.generateToken(loginUser, uuid, refreshTokenExpiredTime, Constant.REFRESH, user.getId()))
        .roles(roles.stream().map(SimpleGrantedAuthority::getAuthority).toList())
        .haveMFA(user.isHaveMfa())
        .build();
  }


  @Override
  public Users saveSsoUser(String email) {

    Users user = Users.builder()
        .userName(email)
        .email(email)
        .createDate(new Date())
        .updateDate(new Date())
        .role(authenticationRoleRepository.findByName(Constant.USER)
            .orElseThrow(() -> new InternalException(ErrorMessage.ROLE_NOT_EXIST)))
        .isSSOUser(Boolean.TRUE)
        .build();

    return authenticationRepository.save(user);
  }

  @Override
  public ByteArrayOutputStream generateOTP(String authenticationToken,
      OTPGenerateRequest otpGenerate) {
    try {
      User user = jwtService.getUserFromJwtToken(authenticationToken)
          .orElseThrow(() -> new NotFoundException(
              ErrorMessage.USER_NOT_EXIST));

      Users users = authenticationRepository.findByUserName(user.getUsername())
          .orElseThrow(() -> new NotFoundException(
              ErrorMessage.USER_NOT_EXIST));

      if (users.isHaveMfa() && isNotRightOtp(users.getOtpSecret(), otpGenerate.getOldOTP())) {
        if (ObjectUtils.isEmpty(otpGenerate.getOldOTP())) {
          throw new BadRequestException(ErrorMessage.OTP_NOT_CORRECT);
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
            ErrorMessage.USER_NOT_EXIST));
    Users users = authenticationRepository.findByUserName(user.getUsername())
        .orElseThrow(() -> new NotFoundException(
            ErrorMessage.USER_NOT_EXIST));

    if (!users.isHaveMfa()) {
      if (isNotRightOtp(users.getOtpSecret(), otpCode)) {
        throw new BadRequestException(ErrorMessage.OTP_NOT_CORRECT);
      }

      users.setHaveMfa(Boolean.TRUE);
      authenticationRepository.save(users);
    }
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
