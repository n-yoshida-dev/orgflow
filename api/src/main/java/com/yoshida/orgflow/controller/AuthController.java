package com.yoshida.orgflow.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.yoshida.orgflow.dto.auth.LoginRequest;
import com.yoshida.orgflow.dto.auth.LoginResponse;

import jakarta.validation.Valid;

@RestController
public class AuthController {
  @PostMapping("/login")
  public LoginResponse login(@Valid @RequestBody LoginRequest request) {
    return new LoginResponse("dummy-token", "Bearer");
  }

}
