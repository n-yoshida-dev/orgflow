package com.yoshida.orgflow.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoshida.orgflow.entity.TenantMembership;

@Repository
public interface TenantMembershipRepository extends JpaRepository<TenantMembership, UUID> {
  boolean existsByUserIdAndTenantId(UUID userId, UUID tenantId);

  List<TenantMembership> findByUserId(UUID userId);
}
