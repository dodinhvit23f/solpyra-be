package com.shopizer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopizer.common.dto.response.Response;
import com.shopizer.constant.ApplicationMessage.ErrorMessage;
import com.shopizer.domain.authentication.services.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Order(0)
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

  @Value("${application.white-list}")
  Set<String> whiteList;

  final JwtService jwtService;
  final HandlerExceptionResolver handlerExceptionResolver;
  final ObjectMapper objectMapper;
  final MessageSource messageSource;

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService,
      HandlerExceptionResolver handlerExceptionResolver, ObjectMapper objectMapper,
      MessageSource messageSource) {
    super(authenticationManager);
    this.jwtService = jwtService;
    this.handlerExceptionResolver = handlerExceptionResolver;
    this.objectMapper = objectMapper;
    this.messageSource = messageSource;
  }


  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    AntPathMatcher pathMatcher = new AntPathMatcher();

    return whiteList.stream()
        .anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()));
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);
    try {
      if (ObjectUtils.isEmpty(jwtToken) ||
          !jwtService.validateJwtToken(jwtToken)) {
        throw new AuthenticationCredentialsNotFoundException(
            ErrorMessage.INVAlID_TOKEN);
      }

      User userDetail = jwtService.getUserFromJwtToken(jwtToken)
          .orElseThrow(() -> new AuthenticationCredentialsNotFoundException(
              ErrorMessage.INVAlID_TOKEN));

      UsernamePasswordAuthenticationToken authenticationToken =
          new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
      authenticationToken.setDetails(new WebAuthenticationDetails(request));

      SecurityContextHolder.getContext().setAuthentication(authenticationToken);

      doFilter(request, response, chain);
    } catch (AuthenticationCredentialsNotFoundException | ExpiredJwtException
             | MalformedJwtException | SignatureException e) {

      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.setContentType("application/json");
      response.getWriter().write(objectMapper.writeValueAsString(Response.<Object>builder()
          .errorCodes(Set.of(ErrorMessage.INVAlID_TOKEN))
          .extraMessage(Map.of(ErrorMessage.INVAlID_TOKEN,
              messageSource.getMessage(ErrorMessage.INVAlID_TOKEN, null,
                  Locale.getDefault())))
          .traceId(UUID.randomUUID().toString())
          .build()));
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

  }
}
