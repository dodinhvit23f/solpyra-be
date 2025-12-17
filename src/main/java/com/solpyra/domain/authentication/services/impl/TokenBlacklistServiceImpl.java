package com.solpyra.domain.authentication.services.impl;

import com.solpyra.domain.authentication.services.JwtService;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenBlacklistServiceImpl {

  final Map<String, LocalDateTime> blackListAccessToken = new ConcurrentHashMap<>();
  final JwtService jwtService;

  public boolean inBlackList(String token) {
    return blackListAccessToken.containsKey(token);
  }

  //TODO: Check blackListExpired
}
