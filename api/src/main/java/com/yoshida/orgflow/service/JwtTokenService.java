package com.yoshida.orgflow.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.yoshida.orgflow.config.JwtProperties;

@Service
public class JwtTokenService {
  private final JwtProperties jwtProperties;
  private final JwtEncoder jwtEncoder;

  public JwtTokenService(JwtProperties jwtProperties, JwtEncoder jwtEncoder) {
    this.jwtProperties = jwtProperties;
    this.jwtEncoder = jwtEncoder;
  }

  public String generateAccessToken(UUID userId) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(jwtProperties.getAccessTokenExpiresIn());

    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer(jwtProperties.getIssuer())
        .issuedAt(now)
        .expiresAt(expiresAt)
        .subject(userId.toString())
        .build();

    JwtEncoderParameters parameters = JwtEncoderParameters.from(jwsHeader, claims);
    Jwt jwt = jwtEncoder.encode(parameters);

    return jwt.getTokenValue();
  }

  public String generateAccessTokenWithCurrentTenant(UUID userId, UUID tenantId) {
    Instant now = Instant.now();
    Instant expiresAt = now.plus(jwtProperties.getAccessTokenExpiresIn());

    JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).build();

    JwtClaimsSet claims = JwtClaimsSet.builder()
        .issuer(jwtProperties.getIssuer())
        .issuedAt(now)
        .expiresAt(expiresAt)
        .subject(userId.toString())
        .claim("current_tenant_id", tenantId.toString())
        .build();

    JwtEncoderParameters parameters = JwtEncoderParameters.from(jwsHeader, claims);
    Jwt jwt = jwtEncoder.encode(parameters);

    return jwt.getTokenValue();
  }
}
