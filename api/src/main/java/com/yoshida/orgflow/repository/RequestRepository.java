package com.yoshida.orgflow.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yoshida.orgflow.entity.Request;

@Repository
public interface RequestRepository extends JpaRepository<Request, UUID> {

  /**
   * ユーザーが、指定 tenant 内の指定 internal_organization に所属しているか。
   * false = 申請元として指定された組織が存在しないか、ユーザーが所属していない（入力値が不正 → 422）。
   */
  @Query(value = """
      SELECT EXISTS (
        SELECT 1
        FROM
          internal_organization_memberships iom
          JOIN tenant_memberships tm
            ON tm.id = iom.tenant_membership_id
            AND tm.tenant_id = iom.tenant_id
        WHERE iom.tenant_id = :tenantId
          AND tm.user_id = :userId
          AND iom.internal_organization_id = :internalOrganizationId
      )""", nativeQuery = true)
  boolean existsInternalOrganizationMembership(
      @Param("userId") UUID userId,
      @Param("tenantId") UUID tenantId,
      @Param("internalOrganizationId") UUID internalOrganizationId);

  /**
   * 上記の所属に requester ロールが付与されているか。
   * false = 所属はあるが申請権限がない（認証済みだが権限不足 → 403）。
   */
  @Query(value = """
      SELECT EXISTS (
        SELECT 1
        FROM
          internal_organization_memberships iom
          JOIN tenant_memberships tm
            ON tm.id = iom.tenant_membership_id
            AND tm.tenant_id = iom.tenant_id
          JOIN internal_organization_membership_roles iomr
            ON iomr.internal_organization_membership_id = iom.id
          JOIN internal_organization_roles ior
            ON ior.id = iomr.role_id
            AND ior.role_name = 'requester'
        WHERE iom.tenant_id = :tenantId
          AND tm.user_id = :userId
          AND iom.internal_organization_id = :internalOrganizationId
      )""", nativeQuery = true)
  boolean hasRequesterRole(
      @Param("userId") UUID userId,
      @Param("tenantId") UUID tenantId,
      @Param("internalOrganizationId") UUID internalOrganizationId);
}
