package com.yoshida.orgflow.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yoshida.orgflow.dto.request.CreateRequestInput;
import com.yoshida.orgflow.dto.request.RequestResponse;
import com.yoshida.orgflow.security.JwtCurrentTenantIdExtractor;
import com.yoshida.orgflow.security.JwtUserIdExtractor;
import com.yoshida.orgflow.service.RequestService;

import jakarta.validation.Valid;

@RestController
public class RequestController {

  private final RequestService requestService;
  private final JwtUserIdExtractor jwtUserIdExtractor;
  private final JwtCurrentTenantIdExtractor jwtCurrentTenantIdExtractor;

  public RequestController(RequestService requestService,
      JwtUserIdExtractor jwtUserIdExtractor,
      JwtCurrentTenantIdExtractor jwtCurrentTenantIdExtractor) {
    this.requestService = requestService;
    this.jwtUserIdExtractor = jwtUserIdExtractor;
    this.jwtCurrentTenantIdExtractor = jwtCurrentTenantIdExtractor;
  }

  @PostMapping("/requests")
  @ResponseStatus(HttpStatus.CREATED)
  public RequestResponse createRequest(@AuthenticationPrincipal Jwt jwt,
      @Valid @RequestBody CreateRequestInput input) {
    UUID userId = jwtUserIdExtractor.extractUserId(jwt);
    UUID tenantId = jwtCurrentTenantIdExtractor.extractCurrentTenantId(jwt);
    return requestService.createDraft(userId, tenantId, input);
  }
}
