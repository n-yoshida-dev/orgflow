package com.yoshida.orgflow.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.yoshida.orgflow.dto.tenant.TenantResponse;
import com.yoshida.orgflow.entity.TenantMembership;
import com.yoshida.orgflow.repository.TenantMembershipRepository;
import com.yoshida.orgflow.repository.TenantRepository;

@Service
public class TenantService {

  private final TenantRepository tenantRepository;
  private final TenantMembershipRepository tenantMembershipRepository;

  public TenantService(
      TenantRepository tenantRepository,
      TenantMembershipRepository tenantMembershipRepository) {
    this.tenantRepository = tenantRepository;
    this.tenantMembershipRepository = tenantMembershipRepository;

  }

  public List<TenantResponse> listTenantsForUser(UUID userId) {
    List<TenantMembership> memberships = tenantMembershipRepository.findByUserId(userId);

    if (memberships.isEmpty()) {
      return List.of();
    }

    List<UUID> tenantIds = memberships.stream()
        .map(TenantMembership::getTenantId)
        .toList();

    return tenantRepository.findAllById(tenantIds).stream()
        .map(tenant -> new TenantResponse(tenant.getId(), tenant.getName()))
        .toList();

  }
}
