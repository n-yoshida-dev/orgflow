package com.yoshida.orgflow.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.yoshida.orgflow.common.exception.InternalOrganizationMembershipNotFoundException;
import com.yoshida.orgflow.common.exception.RequesterRoleNotGrantedException;
import com.yoshida.orgflow.dto.request.CreateRequestInput;
import com.yoshida.orgflow.dto.request.RequestResponse;
import com.yoshida.orgflow.entity.AuditLog;
import com.yoshida.orgflow.entity.InternalOrganization;
import com.yoshida.orgflow.entity.Request;
import com.yoshida.orgflow.entity.User;
import com.yoshida.orgflow.repository.AuditLogRepository;
import com.yoshida.orgflow.repository.InternalOrganizationRepository;
import com.yoshida.orgflow.repository.RequestRepository;
import com.yoshida.orgflow.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RequestService {
  private final RequestRepository requestRepository;
  private final UserRepository userRepository;
  private final InternalOrganizationRepository internalOrganizationRepository;
  private final AuditLogRepository auditLogRepository;

  public RequestService(RequestRepository requestRepository,
      UserRepository userRepository,
      InternalOrganizationRepository internalOrganizationRepository,
      AuditLogRepository auditLogRepository) {
    this.requestRepository = requestRepository;
    this.userRepository = userRepository;
    this.internalOrganizationRepository = internalOrganizationRepository;
    this.auditLogRepository = auditLogRepository;
  }

  public RequestResponse createDraft(UUID userId, UUID tenantId, CreateRequestInput input) {
    if (!requestRepository.existsInternalOrganizationMembership(userId, tenantId, input.internalOrganizationId())) {
      throw new InternalOrganizationMembershipNotFoundException("ユーザーは組織に所属していません");
    }

    if (!requestRepository.hasRequesterRole(userId, tenantId, input.internalOrganizationId())) {
      throw new RequesterRoleNotGrantedException("ユーザーにはロールが付いていません");
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));
    InternalOrganization internalOrganization = internalOrganizationRepository
        .findByIdAndTenantId(input.internalOrganizationId(), tenantId)
        .orElseThrow(() -> new IllegalArgumentException("組織が見つかりません"));

    Request request = Request.createDraft(
        tenantId,
        user.getId(),
        internalOrganization.getId(),
        input.requestType(),
        input.title(),
        null, // description は null で初期化
        input.amount());

    requestRepository.save(request);

    AuditLog auditLog = AuditLog.record(tenantId, userId, user.getDisplayName(), internalOrganization.getId(),
        internalOrganization.getInternalOrganizationName(), user.getMailAddress(), "request", request.getId(),
        "create");
    auditLogRepository.save(auditLog);

    return new RequestResponse(
        request.getId(),
        request.getTenantId(),
        request.getRequestSeriesId(),
        request.getVersionNo(),
        request.getApplicantUserId(),
        request.getInternalOrganizationId(),
        request.getStatus(),
        request.getCurrentAppliedApprovalRouteStepId(),
        request.getRequestType(),
        request.getTitle(),
        request.getDescription(),
        request.getAmount(),
        request.getCreatedAt(),
        request.getUpdatedAt());
  }
}
