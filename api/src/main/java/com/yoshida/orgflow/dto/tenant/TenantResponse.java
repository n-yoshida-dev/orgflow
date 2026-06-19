package com.yoshida.orgflow.dto.tenant;

import java.util.UUID;

public record TenantResponse(
    UUID tenantId,
    String tenantName) {

}
