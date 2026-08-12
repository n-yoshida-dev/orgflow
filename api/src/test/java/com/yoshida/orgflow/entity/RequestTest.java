package com.yoshida.orgflow.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * {@link Request#createDraft} の単体テスト。
 * 依存を持たない static ファクトリなので mock は使わない。
 */
public class RequestTest {

  private UUID tenantId;
  private UUID applicantUserId;
  private UUID internalOrganizationId;

  @BeforeEach
  public void setUp() {
    tenantId = UUID.randomUUID();
    applicantUserId = UUID.randomUUID();
    internalOrganizationId = UUID.randomUUID();
  }

  @Test
  void 渡した値がそのまま設定される() {
    Request request = Request.createDraft(tenantId, applicantUserId, internalOrganizationId, "transportation_expenses",
        "テスト申請",
        "testDescription", 100);

    assertEquals(tenantId, request.getTenantId());
    assertEquals("テスト申請", request.getTitle());
    assertEquals("testDescription", request.getDescription());
    assertEquals(100, request.getAmount());
    assertEquals(applicantUserId, request.getApplicantUserId());
    assertEquals(internalOrganizationId, request.getInternalOrganizationId());
  }

  @Test
  void 固定値の検証() {
    Request request = Request.createDraft(tenantId, applicantUserId, internalOrganizationId, "transportation_expenses",
        "テスト申請",
        "testDescription", 100);

    assertEquals("draft", request.getStatus());
    assertEquals(1, request.getVersionNo());
    assertNull(request.getCurrentAppliedApprovalRouteStepId());
  }
}
