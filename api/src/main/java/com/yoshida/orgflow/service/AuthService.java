package com.yoshida.orgflow.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yoshida.orgflow.common.exception.AuthenticationFailedException;
import com.yoshida.orgflow.common.exception.TenantMembershipNotFoundException;
import com.yoshida.orgflow.dto.auth.LoginResponse;
import com.yoshida.orgflow.dto.auth.TenantSelectResponse;
import com.yoshida.orgflow.entity.User;
import com.yoshida.orgflow.repository.TenantMembershipRepository;
import com.yoshida.orgflow.repository.UserRepository;

@Service
public class AuthService {

  private static final String AUTHENTICATION_FAILED_MESSAGE = "loginId または password が正しくありません";
  private static final String TENANT_MEMBERSHIP_NOT_FOUND_MESSAGE = "ユーザーはテナントのメンバーではありません";

  private final UserRepository userRepository;
  private final TenantMembershipRepository tenantMembershipRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenService jwtTokenService;

  public AuthService(
      UserRepository userRepository,
      TenantMembershipRepository tenantMembershipRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenService jwtTokenService) {
    this.userRepository = userRepository;
    this.tenantMembershipRepository = tenantMembershipRepository;
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

    return new LoginResponse(accessToken);
  }

  public TenantSelectResponse selectTenant(UUID userId, UUID tenantId) {
    if (!tenantMembershipRepository.existsByUserIdAndTenantId(userId, tenantId)) {
      throw new TenantMembershipNotFoundException(TENANT_MEMBERSHIP_NOT_FOUND_MESSAGE);
    }

    String accessToken = jwtTokenService.generateAccessTokenWithCurrentTenant(userId, tenantId);

    return new TenantSelectResponse(accessToken);
  }
}
