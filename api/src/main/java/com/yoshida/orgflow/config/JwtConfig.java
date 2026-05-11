package com.yoshida.orgflow.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

@Configuration
public class JwtConfig {

  private final JwtProperties jwtProperties;

  public JwtConfig(JwtProperties jwtProperties) {
    this.jwtProperties = jwtProperties;
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    byte[] secret = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);

    SecretKeySpec secretKey = new SecretKeySpec(secret, "HmacSHA256");

    return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey));

  }

}
