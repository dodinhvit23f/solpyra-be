package com.solpyra.domain.authentication.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import java.io.IOException;
import java.security.GeneralSecurityException;

public interface GoogleDriverService {

  GoogleIdToken getGoogleIdToken(String idToken)
      throws GeneralSecurityException, IOException;
}
