package com.yoshida.orgflow.dto.request;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RequestResponse(
    UUID id,
    UUID tenantId,
    UUID requestSeriesId,
    Integer versionNo,
    UUID applicantUserId,
    UUID internalOrganizationId,
    String status,
    UUID currentAppliedApprovalRouteStepId,
    String requestType,
    String title,
    String description,
    Integer amount,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt) {
}
