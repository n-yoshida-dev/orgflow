package com.yoshida.orgflow.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.yoshida.orgflow.entity.InternalOrganization;

@Repository
public interface InternalOrganizationRepository extends JpaRepository<InternalOrganization, UUID> {

  /**
   * tenant を条件に含めて取得する。id だけで引くと他 tenant の組織を取り得るため、
   * tenant 境界はクエリ側でも強制する。
   */
  Optional<InternalOrganization> findByIdAndTenantId(UUID id, UUID tenantId);
}
