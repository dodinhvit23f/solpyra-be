package com.shopizer.domain.authentication.services.impl;

import com.shopizer.constant.ApplicationMessage;
import com.shopizer.constant.Constant;
import com.shopizer.domain.authentication.services.JwtService;
import com.shopizer.util.Utils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JwtServiceImpl implements JwtService {

  final SecretKey secretKeyJwt;

  @Override
  public Optional<User> getUserFromJwtToken(String token) throws UsernameNotFoundException {
    Claims claims = getClaims(token);

    String userName = claims.get(Constant.USER_DETAIL, String.class);
    String password = claims.get(Constant.PASSWORD, String.class);

    if (ObjectUtils.isEmpty(claims.get(Constant.AUTHORITY))) {
      throw new UsernameNotFoundException(ApplicationMessage.AuthenticationMessage.AUTHORITY_IS_EMPTY);
    }

    List<SimpleGrantedAuthority> authorities = claims.get(Constant.AUTHORITY, List.class).stream()
        .map(authority -> new SimpleGrantedAuthority(authority.toString()))
        .toList();

    return Optional.of(new User(userName, password, authorities));
  }

  @Override
  public Optional<String> getUsernameFromJwtToken(String token) {
    Claims claims = getClaims(token);
    return Optional.ofNullable(claims.get(Constant.USER_DETAIL, String.class));
  }

  @Override
  public boolean validateJwtToken(String authToken) {
    try {
      Jwts.parser()
          .verifyWith(secretKeyJwt)
          .build()
          .parseSignedClaims(authToken.replace(Constant.BEARER, "")).getPayload();
      return Boolean.TRUE;
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return Boolean.FALSE;
  }

  @Override
  public boolean validateRefreshJwtToken(String refreshToken) {
    try {
      return getClaims(refreshToken).get(Constant.TYPE).equals(Constant.REFRESH);
    } catch (IllegalArgumentException e) {
      log.error("JWT claims string is empty: {}", e.getMessage());
    }

    return Boolean.FALSE;
  }

  @Override
  public String generateToken(User userDetails, String uuid, int times, String type) {
    Date now = Date.from(Instant.now());
    Date expiryDate = Utils.plusDate(now, Calendar.MINUTE, times);

    Map<String, Object> claims = new HashMap<>();

    claims.put(Constant.USER_DETAIL, userDetails.getUsername());
    claims.put(Constant.AUTHORITY,
        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet()));
    claims.put(Constant.PASSWORD, userDetails.getPassword());
    claims.put(Constant.UUID, uuid);
    claims.put(Constant.TYPE, type);

    return Jwts.builder()
        .claims(claims)
        .issuedAt(now)
        .expiration(expiryDate)
        .audience()
        .and()
        .signWith(secretKeyJwt)
        .compact();
  }

  private Claims getClaims(String token) {
    return Jwts.parser()
        .verifyWith(secretKeyJwt)
        .build()
        .parseSignedClaims(token.replace(Constant.BEARER, ""))
        .getPayload();
  }
}
