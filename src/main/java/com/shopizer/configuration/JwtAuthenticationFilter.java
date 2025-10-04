package com.shopizer.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopizer.constant.ApplicationMessage;
import com.shopizer.domain.authentication.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

  public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService,
      HandlerExceptionResolver handlerExceptionResolver, ObjectMapper objectMapper) {
    super(authenticationManager);
    this.jwtService = jwtService;
    this.handlerExceptionResolver = handlerExceptionResolver;
    this.objectMapper = objectMapper;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    AntPathMatcher pathMatcher = new AntPathMatcher();
    String uri = request.getRequestURI();

    boolean isWhitelisted = whiteList.stream()
        .anyMatch(pattern -> pathMatcher.match(pattern, uri));


      if (!isWhitelisted &&
          !request.getMethod().equalsIgnoreCase(HttpMethod.OPTIONS.name())) {
        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (ObjectUtils.isEmpty(jwtToken) ||
            !jwtService.validateJwtToken(jwtToken, request.getRemoteAddr())) {
          throw new UsernameNotFoundException(
              ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST);
        }

        User userDetail = jwtService.getUserFromJwtToken(jwtToken)
            .orElseThrow(() -> new UsernameNotFoundException(
                ApplicationMessage.AuthenticationMessage.USER_NOT_EXIST));

        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(userDetail, null, userDetail.getAuthorities());
        authenticationToken.setDetails(new WebAuthenticationDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
      }

      super.doFilterInternal(request, response, chain);
  }
}
