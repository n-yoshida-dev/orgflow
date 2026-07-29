package com.yoshida.orgflow.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

  /** JPA が DB から読み出すときに使う。アプリコードからは呼ばない。 */
  protected AuditLog() {
  }

  /**
   * 監査ログを新規作成する。id・発生時刻はここで確定する。
   * actorInternalOrganizationId / Name は所属が無い操作では null 可。
   */
  public static AuditLog record(
      UUID actorTenantId,
      UUID actorUserId,
      String actorUserDisplayName,
      UUID actorInternalOrganizationId,
      String actorInternalOrganizationName,
      String actorUserMailAddress,
      String targetType,
      UUID targetId,
      String operationType) {
    AuditLog log = new AuditLog();
    log.id = UUID.randomUUID();
    log.actorTenantId = actorTenantId;
    log.actorUserId = actorUserId;
    log.actorUserDisplayName = actorUserDisplayName;
    log.actorInternalOrganizationId = actorInternalOrganizationId;
    log.actorInternalOrganizationName = actorInternalOrganizationName;
    log.actorUserMailAddress = actorUserMailAddress;
    log.targetType = targetType;
    log.targetId = targetId;
    log.operationType = operationType;
    log.occurredAt = OffsetDateTime.now();
    return log;
  }

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "actor_tenant_id", nullable = false)
  private UUID actorTenantId;

  @Column(name = "actor_user_id", nullable = false)
  private UUID actorUserId;

  @Column(name = "actor_user_display_name", nullable = false)
  private String actorUserDisplayName;

  @Column(name = "actor_internal_organization_id")
  private UUID actorInternalOrganizationId;

  @Column(name = "actor_internal_organization_name")
  private String actorInternalOrganizationName;

  @Column(name = "actor_user_mail_address", nullable = false)
  private String actorUserMailAddress;

  @Column(name = "target_type", nullable = false, length = 100)
  private String targetType;

  @Column(name = "target_id", nullable = false)
  private UUID targetId;

  @Column(name = "operation_type", nullable = false)
  private String operationType;

  @Column(name = "occurred_at", nullable = false)
  private OffsetDateTime occurredAt;

  public UUID getId() {
    return id;
  }

  public UUID getActorTenantId() {
    return actorTenantId;
  }

  public UUID getActorUserId() {
    return actorUserId;
  }

  public String getActorUserDisplayName() {
    return actorUserDisplayName;
  }

  public UUID getActorInternalOrganizationId() {
    return actorInternalOrganizationId;
  }

  public String getActorInternalOrganizationName() {
    return actorInternalOrganizationName;
  }

  public String getActorUserMailAddress() {
    return actorUserMailAddress;
  }

  public String getTargetType() {
    return targetType;
  }

  public UUID getTargetId() {
    return targetId;
  }

  public String getOperationType() {
    return operationType;
  }

  public OffsetDateTime getOccurredAt() {
    return occurredAt;
  }

}
