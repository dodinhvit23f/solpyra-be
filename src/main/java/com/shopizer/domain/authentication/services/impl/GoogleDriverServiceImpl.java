package com.shopizer.domain.authentication.services.impl;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.shopizer.domain.authentication.services.GoogleDriverService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleDriverServiceImpl implements GoogleDriverService {

  final GoogleIdTokenVerifier verifier;

  @Override
  public GoogleIdToken getGoogleIdToken(String idToken)
      throws GeneralSecurityException, IOException {
    GoogleIdToken googleIdToken = verifier.verify(idToken);
    return googleIdToken;
  }
}
