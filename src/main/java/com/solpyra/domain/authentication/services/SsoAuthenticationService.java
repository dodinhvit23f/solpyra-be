package com.solpyra.domain.authentication.services;

import com.solpyra.domain.authentication.dto.request.GoogleSsoSignInRequest;
import com.solpyra.domain.authentication.dto.response.SignInResponse;
import com.solpyra.exception.NotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface SsoAuthenticationService {

  SignInResponse verifyGoogleIdToken(GoogleSsoSignInRequest request)
      throws GeneralSecurityException, IOException, NotFoundException;
}
