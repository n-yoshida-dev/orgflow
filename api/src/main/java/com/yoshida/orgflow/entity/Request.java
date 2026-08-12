package com.yoshida.orgflow.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "requests")
public class Request {

  /** JPA が DB から読み出すときに使う。アプリコードからは呼ばない。 */
  protected Request() {
  }

  /**
   * draft 申請を新規作成する。id・日時・固定値（version=1, status=draft）はここで確定する。
   * current_applied_approval_route_step_id は draft では null のまま。
   */
  public static Request createDraft(
      UUID tenantId,
      UUID applicantUserId,
      UUID internalOrganizationId,
      String requestType,
      String title,
      String description,
      Integer amount) {
    Request r = new Request();
    r.id = UUID.randomUUID();
    r.tenantId = tenantId;
    r.requestSeriesId = UUID.randomUUID();
    r.versionNo = 1;
    r.applicantUserId = applicantUserId;
    r.internalOrganizationId = internalOrganizationId;
    r.status = "draft";
    r.currentAppliedApprovalRouteStepId = null;
    r.requestType = requestType;
    r.title = title;
    r.description = description;
    r.amount = amount;
    OffsetDateTime now = OffsetDateTime.now();
    r.createdAt = now;
    r.updatedAt = now;
    return r;
  }

  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "tenant_id", nullable = false)
  private UUID tenantId;

  @Column(name = "request_series_id", nullable = false)
  private UUID requestSeriesId;

  @Column(name = "version_no", nullable = false)
  private Integer versionNo;

  @Column(name = "applicant_user_id", nullable = false)
  private UUID applicantUserId;

  @Column(name = "internal_organization_id", nullable = false)
  private UUID internalOrganizationId;

  @Column(name = "status", nullable = false)
  private String status;

  @Column(name = "current_applied_approval_route_step_id")
  private UUID currentAppliedApprovalRouteStepId;

  @Column(name = "request_type", nullable = false)
  private String requestType;

  @Column(name = "title", nullable = false, length = 100)
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "amount", nullable = false)
  private Integer amount;

  @Column(name = "created_at", nullable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  public UUID getId() {
    return id;
  }

  public UUID getTenantId() {
    return tenantId;
  }

  public UUID getRequestSeriesId() {
    return requestSeriesId;
  }

  public Integer getVersionNo() {
    return versionNo;
  }

  public UUID getApplicantUserId() {
    return applicantUserId;
  }

  public UUID getInternalOrganizationId() {
    return internalOrganizationId;
  }

  public String getStatus() {
    return status;
  }

  public UUID getCurrentAppliedApprovalRouteStepId() {
    return currentAppliedApprovalRouteStepId;
  }

  public String getRequestType() {
    return requestType;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public Integer getAmount() {
    return amount;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

}