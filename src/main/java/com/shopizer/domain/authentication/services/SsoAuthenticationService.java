package com.shopizer.domain.authentication.services;

import com.shopizer.domain.authentication.dto.request.GoogleSsoSignInRequest;
import com.shopizer.domain.authentication.dto.response.SignInResponse;
import com.shopizer.exception.NotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface SsoAuthenticationService {

  SignInResponse verifyGoogleIdToken(GoogleSsoSignInRequest request)
      throws GeneralSecurityException, IOException, NotFoundException;
}
