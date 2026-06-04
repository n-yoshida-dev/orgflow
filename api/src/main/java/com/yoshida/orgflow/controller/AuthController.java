package com.yoshida.orgflow.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yoshida.orgflow.dto.auth.LoginRequest;
import com.yoshida.orgflow.dto.auth.LoginResponse;
import com.yoshida.orgflow.dto.auth.TenantSelectResponse;
import com.yoshida.orgflow.security.JwtUserIdExtractor;
import com.yoshida.orgflow.service.AuthService;

import jakarta.validation.Valid;

@RestController
public class AuthController {

  private final AuthService authService;
  private final JwtUserIdExtractor jwtUserIdExtractor;

  public AuthController(AuthService authService, JwtUserIdExtractor jwtUserIdExtractor) {
    this.authService = authService;
    this.jwtUserIdExtractor = jwtUserIdExtractor;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request.loginId(), request.password());
  }

  @PostMapping("/tenants/{tenantId}/select")
  public TenantSelectResponse selectTenant(@AuthenticationPrincipal Jwt jwt,
      @PathVariable UUID tenantId) {
    UUID userId = jwtUserIdExtractor.extractUserId(jwt);
    return authService.selectTenant(userId, tenantId);
  }

}
