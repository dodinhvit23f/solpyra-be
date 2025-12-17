package com.solpyra.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "google_credential")
@Getter
@Setter
public class GoogleCredential extends BaseEntity {

  @Column(name = "client_id")
  private String clientId;
  @Column(name = "client_secret")
  private String clientSecret;
  @Column(name = "google_token")
  private String googleToken;
  @Column(name = "google_access_token")
  private String googleAccessToken;
  @Column(name = "google_refresh_token")
  private String googleRefreshToken;
  @Column(name = "expires_at")
  private ZonedDateTime expiresAt;

}