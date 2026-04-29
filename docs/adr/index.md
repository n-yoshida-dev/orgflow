# ADR Index

このディレクトリは、OrgFlow の設計判断を記録した ADR（Architecture Decision Record）の一覧です。
各 ADR には、採用した案、不採用にした案、受け入れる制約、見直す条件を残します。

## 一覧

- [ADR-001](./ADR-001-なぜ題材をタスク管理ではなく申請・承認にしたか.md)
  題材として申請・承認アプリを採用する

- [ADR-002](./ADR-002-監査ログを独立した概念として扱う.md)
  監査ログを独立した概念として扱う

- [ADR-003](./ADR-003-RBACの最小方針.md)
  RBAC の最小方針

- [ADR-004](./ADR-004-業務フローの状態遷移をどう表現するか.md)
  業務フローの状態遷移は明示的な状態モデルで表現する

- [ADR-005](./ADR-005-requestの申請者・組織の参照方式.md)
  request の申請者・組織の参照方式

- [ADR-006](./ADR-006-applied_approval_routeの保存構造.md)
  applied_approval_route の保存構造

- [ADR-007](./ADR-007-requestの版管理方式.md)
  request の版管理方式

- [ADR-008](./ADR-008-承認ルートの適用結果をrequestに紐づけて保持する.md)
  承認ルートの適用結果を request に紐づけて保持する

- [ADR-009](./ADR-009-applied_approval_routeを親子構造で保持する.md)
  applied_approval_route を親子構造で保持する

- [ADR-010](./ADR-010-同一承認段に複数承認者がいる場合のルール.md)
  同一承認段に複数承認者がいる場合のルール

- [ADR-011](./ADR-011-audit_logの操作対象参照方式.md)
  audit_log の操作対象参照方式

- [ADR-012](./ADR-012-approval_policyとapproval_routeの参照方向.md)
  approval_policy と approval_route の参照方向

- [ADR-013](./ADR-013-tenantとinternal_organizationを分離する.md)
  tenant と internal organization を分離する

- [ADR-014](./ADR-014-tenantスコープ権限とinternal_organizationスコープ権限を分離する.md)
  tenant スコープ権限と internal organization スコープ権限を分離する

- [ADR-015](./ADR-015-shared-db前提でtenant_idにより分離する.md)
  shared DB 前提で tenant_id により分離する

- [ADR-016](./ADR-016-認証方式の選定理由.md)
  Bearer token を用いる認証済み API として設計する

- [ADR-017](./ADR-017-OpenAPIをAPI契約としてv1に絞って先に管理する理由.md)
  OpenAPI を API 契約として v1 の重要 endpoint から先に管理する

- [ADR-018](./ADR-018-ローカル開発DBはDocker ComposeのPostgreSQLで起動する.md)
  ローカル開発DBを Docker Compose の PostgreSQL で起動する

## テンプレート

新しい ADR を作成するときは [ひな形](./ひな形-ADR-xxx-___.md) を使ってください。
