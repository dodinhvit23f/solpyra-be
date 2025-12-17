package com.solpyra.configuration;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.EntityManager;
import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class AppConfiguration {

  @Value("${application.jwt.secret}")
  private String secret ;

  @Bean(name = "messageResource")
  public MessageSource getMessageSource() {
    ReloadableResourceBundleMessageSource messageResource =
        new ReloadableResourceBundleMessageSource();
    messageResource.setBasename("classpath:messages");
    messageResource.setDefaultEncoding("UTF-8");
    messageResource.setUseCodeAsDefaultMessage(true);
    return messageResource;
  }

  @Bean
  public SecretKey getSigningKeyJwt() {
    byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  @Bean
  public TimeBasedOneTimePasswordGenerator timeBasedOneTimePasswordGenerator() {
    return new TimeBasedOneTimePasswordGenerator();
  }

  @Bean
  public MessageSource messageSource() {
    ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
    messageSource.setBasename("classpath:messages");
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setUseCodeAsDefaultMessage(true);
    return messageSource;
  }

  @Bean
  public JPAQueryFactory jpaQueryFactory(EntityManager em) {
    return new JPAQueryFactory(JPQLTemplates.DEFAULT, em);
  }

}
