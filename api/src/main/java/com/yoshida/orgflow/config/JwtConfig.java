package com.yoshida.orgflow.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.jwt.Jwt;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class JwtConfig {

  private final JwtProperties jwtProperties;

  public JwtConfig(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  private SecretKeySpec createSecretKey() {
    byte[] secret = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(secret, "HmacSHA256");
  }

  @Bean
  public JwtEncoder jwtEncoder() {

    return new NimbusJwtEncoder(new ImmutableSecret<>(createSecretKey()));

  }

  @Bean
  public JwtDecoder jwtDecoder() {
    NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(createSecretKey())
        .macAlgorithm(MacAlgorithm.HS256)
        .build();

    OAuth2TokenValidator<Jwt> jwtValidator = JwtValidators.createDefaultWithIssuer(jwtProperties.getIssuer());

    decoder.setJwtValidator(jwtValidator);

    return decoder;
  }

}
