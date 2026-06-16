package com.yoshida.orgflow.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoshida.orgflow.security.JwtCurrentTenantIdExtractor;
import com.yoshida.orgflow.security.JwtUserIdExtractor;

@RestController
public class HelloAuthController {
  private final JwtUserIdExtractor jwtUserIdExtractor;
  private final JwtCurrentTenantIdExtractor jwtCurrentTenantIdExtractor;

  public HelloAuthController(JwtUserIdExtractor jwtUserIdExtractor,
      JwtCurrentTenantIdExtractor jwtCurrentTenantIdExtractor) {
    this.jwtUserIdExtractor = jwtUserIdExtractor;
    this.jwtCurrentTenantIdExtractor = jwtCurrentTenantIdExtractor;
  }

  @GetMapping("/hello-auth")
  public UUID sayHello(@AuthenticationPrincipal Jwt jwt) {
    UUID userId = jwtUserIdExtractor.extractUserId(jwt);
    return userId;
  }

  public record HelloTenantResponse(
      UUID userId,
      UUID currentTenantId) {
  }

  @GetMapping("/hello-tenant")
  public HelloTenantResponse responseUserIdAndCurrentTenantId(@AuthenticationPrincipal Jwt jwt) {
    UUID userId = jwtUserIdExtractor.extractUserId(jwt);
    UUID currentTenantId = jwtCurrentTenantIdExtractor.extractCurrentTenantId(jwt);
    return new HelloTenantResponse(userId, currentTenantId);
  }
}