package com.yoshida.orgflow.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "internal_organizations")
public class InternalOrganization {

  /** JPA が DB から読み出すときに使う。アプリコードからは呼ばない。 */
  protected InternalOrganization() {
  }

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "internal_organization_name", nullable = false)
  private String internalOrganizationName;

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public String getInternalOrganizationName() {
    return internalOrganizationName;
  }

}
