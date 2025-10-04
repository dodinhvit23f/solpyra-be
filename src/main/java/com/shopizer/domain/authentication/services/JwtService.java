package com.shopizer.domain.authentication.services;

import java.util.Optional;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.User;

public interface JwtService {
    Optional<User> getUserFromJwtToken(String token) throws BadRequestException;

    Optional<String> getUsernameFromJwtToken(String token);

    boolean validateJwtToken(String authToken, String networkIp);

    String generateToken(User userDetails, String uuid, int times, String type);
}
