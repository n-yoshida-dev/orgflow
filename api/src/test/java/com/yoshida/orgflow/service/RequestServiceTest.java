package com.yoshida.orgflow.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

/**
 * {@link RequestService#createDraft} の単体テスト。
 *
 * <p>
 * Repository をすべて mock に差し替えるため、DB も Spring コンテキストも起動しない。
 * 検証対象は Service の制御フロー（検証の順序・例外・保存の有無・監査ログの内容）に限られる。
 *
 * <p>
 * SQL が正しい行を返すか、DB 制約やトランザクションが実際に効くかは
 * このテストでは検証できない（結合テストの領分）。
 */
@ExtendWith(MockitoExtension.class)
public class RequestServiceTest {

  @Mock
  private RequestRepository requestRepository;
  @Mock
  private UserRepository userRepository;
  @Mock
  private InternalOrganizationRepository internalOrganizationRepository;
  @Mock
  private AuditLogRepository auditLogRepository;

  /**
   * User / InternalOrganization は「DB から読むだけ」の設計で、
   * アプリコードから値を入れる手段（setter・public コンストラクタ）を持たない。
   * そのため本物を組み立てられず、getter をスタブした mock で代用する。
   */
  @Mock
  private User user;
  @Mock
  private InternalOrganization internalOrganization;

  @InjectMocks
  private RequestService requestService;

  private UUID tenantId;
  private UUID applicantUserId;
  private UUID internalOrganizationId;
  private CreateRequestInput input;

  @BeforeEach
  public void setUp() {
    // 全テスト共通のテストデータ。値だけを共通化し、
    // 状況の違い（スタブ）は各テストに残す
    tenantId = UUID.randomUUID();
    applicantUserId = UUID.randomUUID();
    internalOrganizationId = UUID.randomUUID();
    input = new CreateRequestInput(
        "Test Request", internalOrganizationId, "transportation_expenses", 100);
  }

  @Test
  public void 組織に所属していない場合はロールチェックに進まず例外を投げて何も保存しない() {
    // 「指定された組織に所属していない」状況を作る
    when(requestRepository.existsInternalOrganizationMembership(applicantUserId, tenantId, internalOrganizationId))
        .thenReturn(false);

    // Act & Assert
    // 所属外の組織を指定された＝入力値が不正なので 422 にマッピングされる例外
    assertThrows(InternalOrganizationMembershipNotFoundException.class,
        () -> requestService.createDraft(applicantUserId, tenantId, input));

    // Assert
    // 検証で弾かれたときは副作用ゼロで止まる（申請も監査ログも書かれない）
    verify(requestRepository, never()).save(any());
    verify(auditLogRepository, never()).save(any());

    // 所属チェックで弾いた時点で止まり、ロールチェックには進まない。
    // 順序が逆になると、所属していない人に 403 を返してしまい、本来の 422 が潰れる。
    verify(requestRepository, never()).hasRequesterRole(any(), any(), any());
  }

  @Test
  public void ロールが無い場合は例外を投げて何も保存しない() {
    // 「所属はあるが requester ロールが付いていない」状況を作る
    when(requestRepository.existsInternalOrganizationMembership(applicantUserId, tenantId, internalOrganizationId))
        .thenReturn(true);
    when(requestRepository.hasRequesterRole(applicantUserId, tenantId, internalOrganizationId))
        .thenReturn(false);

    // Act & Assert
    // 認証済みだが権限が足りない＝403 にマッピングされる例外
    assertThrows(RequesterRoleNotGrantedException.class,
        () -> requestService.createDraft(applicantUserId, tenantId, input));

    // Assert
    verify(requestRepository, never()).save(any());
    verify(auditLogRepository, never()).save(any());
  }

  @Test
  public void 申請とAuditLogが保存されRequestResponseが返る() {
    // 最後まで走りきるので、途中で参照される mock をすべて仕込む
    when(requestRepository.existsInternalOrganizationMembership(applicantUserId, tenantId, internalOrganizationId))
        .thenReturn(true);
    when(requestRepository.hasRequesterRole(applicantUserId, tenantId, internalOrganizationId))
        .thenReturn(true);

    // 監査ログに転記される値。ここがそのまま AuditLog に入るはず
    when(user.getId()).thenReturn(applicantUserId);
    when(user.getDisplayName()).thenReturn("テスト太郎");
    when(user.getMailAddress()).thenReturn("test@example.com");
    when(internalOrganization.getId()).thenReturn(internalOrganizationId);
    when(internalOrganization.getInternalOrganizationName()).thenReturn("テスト組織");

    when(userRepository.findById(applicantUserId)).thenReturn(Optional.of(user));
    when(internalOrganizationRepository.findByIdAndTenantId(internalOrganizationId, tenantId))
        .thenReturn(Optional.of(internalOrganization));

    // Act
    RequestResponse response = requestService.createDraft(applicantUserId, tenantId, input);

    // Assert: 戻り値
    // status と versionNo は createDraft が決める固定値、それ以外は入力の素通し
    assertEquals("draft", response.status());
    assertEquals("Test Request", response.title());
    assertEquals("transportation_expenses", response.requestType());
    assertEquals(tenantId, response.tenantId());
    assertEquals(applicantUserId, response.applicantUserId());

    // Assert: 保存された申請
    // save に渡されたインスタンスを捕まえる。any() では中身を検証できないため
    ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
    verify(requestRepository, times(1)).save(requestCaptor.capture());
    Request savedRequest = requestCaptor.getValue();

    // Assert: 保存された監査ログ
    // AuditLog.record は引数が9個あり、取り違えても型が同じで気づけないものが多い。
    // 引数の素通しではなく、User / InternalOrganization から転記された値まで検証する
    ArgumentCaptor<AuditLog> auditLogCaptor = ArgumentCaptor.forClass(AuditLog.class);
    verify(auditLogRepository, times(1)).save(auditLogCaptor.capture());
    AuditLog savedAuditLog = auditLogCaptor.getValue();

    assertEquals(tenantId, savedAuditLog.getActorTenantId());
    assertEquals(applicantUserId, savedAuditLog.getActorUserId());
    assertEquals("テスト太郎", savedAuditLog.getActorUserDisplayName());
    assertEquals("test@example.com", savedAuditLog.getActorUserMailAddress());
    assertEquals(internalOrganizationId, savedAuditLog.getActorInternalOrganizationId());
    assertEquals("テスト組織", savedAuditLog.getActorInternalOrganizationName());
    assertEquals("request", savedAuditLog.getTargetType());
    assertEquals("create", savedAuditLog.getOperationType());

    // 監査ログが、いま保存した申請そのものを指していること
    assertEquals(savedRequest.getId(), savedAuditLog.getTargetId());
  }
}
