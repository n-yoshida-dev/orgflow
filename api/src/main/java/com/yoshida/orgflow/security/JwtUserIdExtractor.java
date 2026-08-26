package com.yoshida.orgflow.security;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.yoshida.orgflow.common.exception.InvalidJwtSubjectException;

@Component
public class JwtUserIdExtractor {
  public UUID extractUserId(Jwt jwt) {
    String subject = jwt.getSubject();

    if (subject == null || subject.isBlank()) {
      throw new InvalidJwtSubjectException("JWT の subject がありません", null);
    }

    try {
      return UUID.fromString(subject);
    } catch (IllegalArgumentException e) {
      throw new InvalidJwtSubjectException("JWT の subject が UUID として不正です", e);
    }
  }
}