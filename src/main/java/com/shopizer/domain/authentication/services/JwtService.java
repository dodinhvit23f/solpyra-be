package com.shopizer.domain.authentication.services;

import java.util.Optional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface JwtService {
    Optional<User> getUserFromJwtToken(String token) throws UsernameNotFoundException;

    Optional<String> getUsernameFromJwtToken(String token);

    boolean validateJwtToken(String authToken);

  boolean validateRefreshJwtToken(String refreshToken);

  String generateToken(User userDetails, String uuid, int times, String type);
}
