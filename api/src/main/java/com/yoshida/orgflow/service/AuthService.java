package com.yoshida.orgflow.service;

import org.springframework.stereotype.Service;

import com.yoshida.orgflow.dto.auth.LoginResponse;

@Service
public class AuthService {

  public LoginResponse login(String loginId, String password) {
    return new LoginResponse("dummy-token", null);
  }

}
