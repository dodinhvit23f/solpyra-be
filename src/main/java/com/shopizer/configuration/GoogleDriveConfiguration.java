package com.shopizer.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class GoogleDriveConfiguration {

  private static final List<String> SCOPES = List.of(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE);

  @Value("${application.google.credential}")
  String googleCredential;
  @Value("${application.google.client-id}")
  String googleClientId;

  final ObjectMapper objectMapper;

  @Bean
  public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier(NetHttpTransport netHttpTransport) throws GeneralSecurityException, IOException {
    return new GoogleIdTokenVerifier.Builder(
        new GooglePublicKeysManager.Builder(netHttpTransport, GsonFactory.getDefaultInstance()).build())
        .setAudience(Collections.singletonList(googleClientId))
        .setAcceptableTimeSkewSeconds(2629800)
        .build();
  }

  @Bean
  public GoogleCredential googleCredential() throws IOException {
    return GoogleCredential
        .fromStream(
            new ByteArrayInputStream(googleCredential.getBytes()))
        .createScoped(SCOPES);
  }

  @Bean
  public Drive getDrive(NetHttpTransport netHttpTransport, GoogleCredential googleCredential) {
    return new Drive.Builder(netHttpTransport, GsonFactory.getDefaultInstance(), googleCredential)
        .build();
  }
}
