package com.shopizer.domain.authentication.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.shopizer.constant.ApplicationMessage.AuthenticationMessage;
import com.shopizer.domain.authentication.dto.request.GoogleSsoSignInRequest;
import com.shopizer.domain.authentication.dto.response.SignInResponse;
import com.shopizer.domain.authentication.repositories.AuthenticationRepository;
import com.shopizer.domain.authentication.services.AuthenticationService;
import com.shopizer.domain.authentication.services.GoogleDriverService;
import com.shopizer.domain.authentication.services.SsoAuthenticationService;
import com.shopizer.entities.Users;
import com.shopizer.exception.NotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SsoAuthenticationServiceImpl implements SsoAuthenticationService {

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
          AuthenticationMessage.NOT_FOUND_GOOGLE_ID_TOKEN);
    }

    GoogleIdToken.Payload payload = idToken.getPayload();
    String sub = payload.getSubject();                  // stable Google user ID
    String email = (String) payload.get("email");
    boolean emailVerified = Boolean.TRUE.equals(payload.getEmailVerified());

    Users user = authenticationRepository.findByUserName(email)
        .orElseGet(() -> authenticationService.saveSsoUser(email));

      return authenticationService.createJwtToken(user);
  }
}
