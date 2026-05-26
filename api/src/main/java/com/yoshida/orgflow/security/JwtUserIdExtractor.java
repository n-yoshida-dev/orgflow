package com.yoshida.orgflow.security;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
public class JwtUserIdExtractor {
  public UUID extractUserId(Jwt jwt) {
    UUID userId = UUID.fromString(jwt.getSubject());
    return userId;
  }

}
