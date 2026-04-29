package com.yoshida.orgflow.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yoshida.orgflow.dto.auth.LoginRequest;
import com.yoshida.orgflow.dto.auth.LoginResponse;
import com.yoshida.orgflow.service.AuthService;

import jakarta.validation.Valid;

@RestController
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return authService.login(request.loginId(), request.password());
  }

}
