package com.yoshida.orgflow.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yoshida.orgflow.security.JwtUserIdExtractor;

@RestController
@RequestMapping("/hello-auth")
public class HelloAuthController {
  private final JwtUserIdExtractor jwtUserIdExtractor;

  public HelloAuthController(JwtUserIdExtractor jwtUserIdExtractor) {
    this.jwtUserIdExtractor = jwtUserIdExtractor;
  }

  @GetMapping
  public UUID sayHello(@AuthenticationPrincipal Jwt jwt) {
    UUID userId = jwtUserIdExtractor.extractUserId(jwt);
    return userId;
  }
}