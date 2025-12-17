package com.solpyra.domain.authentication.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.solpyra.common.constant.ApplicationMessage.ErrorMessage;
import com.solpyra.domain.authentication.dto.request.GoogleSsoSignInRequest;
import com.solpyra.domain.authentication.dto.response.SignInResponse;
import com.solpyra.domain.authentication.repositories.AuthenticationRepository;
import com.solpyra.domain.authentication.services.AuthenticationService;
import com.solpyra.domain.authentication.services.GoogleDriverService;
import com.solpyra.domain.authentication.services.SsoAuthenticationService;
import com.solpyra.entities.Users;
import com.solpyra.exception.NotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SsoAuthenticationServiceImpl implements SsoAuthenticationService {

  @Value("${application.jwt.end-after}")
  int accessTimeOut;
  @Value("${application.jwt.extra-time}")
  int refreshTimeOut;

  final AuthenticationRepository authenticationRepository;
  final GoogleDriverService googleDriverService;
  final AuthenticationService authenticationService;
  final GoogleIdTokenVerifier googleIdTokenVerifier;


  public SsoAuthenticationServiceImpl(AuthenticationRepository authenticationRepository,
      GoogleDriverService googleDriverService,
      GoogleIdTokenVerifier googleIdTokenVerifier,
      AuthenticationService authenticationService) {

    this.googleDriverService = googleDriverService;
    this.googleIdTokenVerifier = googleIdTokenVerifier;
    this.authenticationRepository = authenticationRepository;
    this.authenticationService = authenticationService;
  }

  @Override
  public SignInResponse verifyGoogleIdToken(GoogleSsoSignInRequest request)
      throws GeneralSecurityException, IOException, NotFoundException {

    GoogleIdToken idToken = googleDriverService.getGoogleIdToken(request.getIdToken());

    if (Objects.isNull(idToken)) {
      throw new AuthenticationCredentialsNotFoundException(
          ErrorMessage.NOT_FOUND_GOOGLE_ID_TOKEN);
    }

    GoogleIdToken.Payload payload = idToken.getPayload();
    String sub = payload.getSubject();                  // stable Google user ID
    String email = (String) payload.get("email");
    boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());

    log.info("Google mail {}, ID {} try to login: ", email, sub);

    if (!emailVerified) {
      log.error("Email {} not verified", email);
      throw new AuthenticationCredentialsNotFoundException(
          ErrorMessage.EMAIL_NOT_VERIFIED);
    }

    Optional<Users> usersOptional =  authenticationRepository.findByUserName(email);

    if(usersOptional.isPresent() && !usersOptional.get().isSSOUser()) {
      throw new NotFoundException(ErrorMessage.USER_NOT_EXIST);
    }

    if(usersOptional.isEmpty()){
      usersOptional = Optional.of(authenticationService.saveSsoUser(email));
    }

    return authenticationService.createJwtToken(usersOptional.get(), accessTimeOut, refreshTimeOut);
  }
}
