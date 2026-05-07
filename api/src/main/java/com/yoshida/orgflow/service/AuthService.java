package com.yoshida.orgflow.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.yoshida.orgflow.common.exception.AuthenticationFailedException;
import com.yoshida.orgflow.dto.auth.LoginResponse;
import com.yoshida.orgflow.entity.User;
import com.yoshida.orgflow.repository.UserRepository;

@Service
public class AuthService {

  private final UserRepository userRepository;

  public AuthService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public LoginResponse login(String loginId, String password) {
    Optional<User> userOptional = userRepository.findByLoginId(loginId);
    if (userOptional.isEmpty()) {
      throw new AuthenticationFailedException("loginId または password が正しくありません");
    }
    return new LoginResponse("dummy-token", null);
  }
}
