package com.solpyra.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GooglePublicKeysManager;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.MemoryDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.UserCredentials;
import com.solpyra.domain.upload.file.repository.GoogleCredentialRepository;
import com.solpyra.entities.GoogleCredential;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ObjectUtils;

@Slf4j
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class GoogleDriveConfiguration {

  private static final List<String> SCOPES = List.of(DriveScopes.DRIVE, DriveScopes.DRIVE_FILE);
  public static final String WEB = "web";

  @Value("${application.google.credential}")
  String googleCredential;
  @Value("${application.google.client-id}")
  String googleClientId;

  final ObjectMapper objectMapper;
  final GoogleCredentialRepository googleCredentialRepository;

  @Bean
  public NetHttpTransport netHttpTransport() throws GeneralSecurityException, IOException {
    return GoogleNetHttpTransport.newTrustedTransport();
  }

  @Bean
  public GoogleIdTokenVerifier googleIdTokenVerifier(NetHttpTransport netHttpTransport)
      throws GeneralSecurityException, IOException {
    return new GoogleIdTokenVerifier.Builder(
        new GooglePublicKeysManager.Builder(netHttpTransport,
            GsonFactory.getDefaultInstance()).build())
        .setAudience(Collections.singletonList(googleClientId))
        .setAcceptableTimeSkewSeconds(2629800)
        .build();
  }

  @Bean
  public UserCredentials googleCredential() throws IOException, GeneralSecurityException {

    List<GoogleCredential> credentials = googleCredentialRepository.findByOne(Pageable.ofSize(1));
    GsonFactory jsonGsonFactory = GsonFactory.getDefaultInstance();

    GoogleClientSecrets googleClientSecrets = GoogleClientSecrets.load(jsonGsonFactory,
        new InputStreamReader(
            new ByteArrayInputStream(googleCredential.getBytes())));

    if (ObjectUtils.isEmpty(credentials)) {
      NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();


      GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
          httpTransport, jsonGsonFactory, googleClientSecrets, SCOPES)
          .setDataStoreFactory(new MemoryDataStoreFactory())
          .setAccessType("offline")          // returns refresh_token on first consent
          .setApprovalPrompt("force")
          .build();

      LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888)
          .build();

      Credential googleCredential = new AuthorizationCodeInstalledApp(flow,
          receiver).authorize(WEB);

      credentials.add(new GoogleCredential());

      saveGoogleAccessToken(credentials.getFirst(), googleCredential);
    }

    GoogleCredential credential = credentials.getFirst();

    UserCredentials userCredentials = UserCredentials
        .newBuilder()
        .setClientId(googleClientSecrets.getDetails().getClientId())
        .setClientSecret(googleClientSecrets.getDetails().getClientSecret())
        .setAccessToken(AccessToken.newBuilder()
            .setExpirationTime(Date.from(credential.getExpiresAt().toInstant()))
            .setTokenValue(credential.getGoogleAccessToken())
            .setScopes(SCOPES)
            .build())
        .setRefreshToken(credential.getGoogleRefreshToken())
        .build();

    if(ZonedDateTime.now().isAfter(credential.getExpiresAt())){
      userCredentials.refreshIfExpired();
      saveGoogleAccessToken(credential, userCredentials);
    }

    return userCredentials;

  }

  @Bean
  public Drive getDrive(NetHttpTransport netHttpTransport, UserCredentials googleCredential) {
    return new Drive.Builder(netHttpTransport, GsonFactory.getDefaultInstance(),
        new HttpCredentialsAdapter(googleCredential))
        .build();
  }

  private void saveGoogleAccessToken(GoogleCredential generalInformation,
      UserCredentials googleCredential) {
    Instant expireTime = Instant.now()
        .plusSeconds(googleCredential.getAccessToken().getExpirationTime().getTime());
    generalInformation.setGoogleAccessToken(googleCredential.getAccessToken().getTokenValue());
    generalInformation.setGoogleRefreshToken(googleCredential.getRefreshToken());
    generalInformation.setExpiresAt(ZonedDateTime.ofInstant(expireTime, ZoneId.systemDefault()));

    googleCredentialRepository.save(generalInformation);
  }

  private void saveGoogleAccessToken(GoogleCredential generalInformation,
      Credential googleCredential) {
    Instant expireTime = Instant.now().plusSeconds(googleCredential.getExpiresInSeconds());
    generalInformation.setGoogleAccessToken(googleCredential.getAccessToken());
    generalInformation.setGoogleRefreshToken(googleCredential.getRefreshToken());
    generalInformation.setExpiresAt(ZonedDateTime.ofInstant(expireTime, ZoneId.systemDefault()));

    googleCredentialRepository.save(generalInformation);
  }
}
