package com.yoshida.orgflow.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoshida.orgflow.dto.tenant.TenantResponse;
import com.yoshida.orgflow.security.JwtUserIdExtractor;
import com.yoshida.orgflow.service.TenantService;

@RestController
public class TenantController {

  private final TenantService tenantService;
  private final JwtUserIdExtractor jwtUserIdExtractor;

  public TenantController(TenantService tenantService, JwtUserIdExtractor jwtUserIdExtractor) {
    this.tenantService = tenantService;
    this.jwtUserIdExtractor = jwtUserIdExtractor;
  }

  @GetMapping("/me/tenants")
  public List<TenantResponse> getTenantsForCurrentUser(@AuthenticationPrincipal Jwt jwt) {
    UUID userId = jwtUserIdExtractor.extractUserId(jwt);
    return tenantService.listTenantsForUser(userId);
  }
}
