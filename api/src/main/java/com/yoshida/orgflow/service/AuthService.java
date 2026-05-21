package com.yoshida.orgflow.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yoshida.orgflow.common.exception.AuthenticationFailedException;
import com.yoshida.orgflow.dto.auth.LoginResponse;
import com.yoshida.orgflow.entity.User;
import com.yoshida.orgflow.repository.UserRepository;

@Service
public class AuthService {

  private static final String AUTHENTICATION_FAILED_MESSAGE = "loginId または password が正しくありません";

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenService jwtTokenService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenService = jwtTokenService;
  }

  public LoginResponse login(String loginId, String password) {
    User user = userRepository.findByLoginId(loginId)
        .orElseThrow(() -> new AuthenticationFailedException(AUTHENTICATION_FAILED_MESSAGE));

    if (!passwordEncoder.matches(password, user.getHashedPassword())) {
      throw new AuthenticationFailedException(AUTHENTICATION_FAILED_MESSAGE);
    }

    String accessToken = jwtTokenService.generateAccessToken(user.getId());

    return new LoginResponse(accessToken, null);
  }
}
