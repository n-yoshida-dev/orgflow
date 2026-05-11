package com.yoshida.orgflow.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
  private String issuer;
  private String secret;
  private Duration accessTokenExpiresIn;

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public Duration getAccessTokenExpiresIn() {
    return accessTokenExpiresIn;
  }

  public void setAccessTokenExpiresIn(Duration accessTokenExpiresIn) {
    this.accessTokenExpiresIn = accessTokenExpiresIn;
  }

}
