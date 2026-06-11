package com.yoshida.orgflow.security;

import java.util.UUID;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import com.yoshida.orgflow.common.exception.CurrentTenantNotSelectedException;
import com.yoshida.orgflow.common.exception.InvalidJwtCurrentTenantIdException;

@Component
public class JwtCurrentTenantIdExtractor {
  public UUID extractCurrentTenantId(Jwt jwt) {
    String tenantId = jwt.getClaim("current_tenant_id");

    if (tenantId == null || tenantId.isBlank()) {
      throw new CurrentTenantNotSelectedException("JWT current_tenant_id claim is missing");
    }

    try {
      return UUID.fromString(tenantId);
    } catch (IllegalArgumentException e) {
      throw new InvalidJwtCurrentTenantIdException("JWT current_tenant_id claim is not a valid UUID", e);
    }
  }

}
