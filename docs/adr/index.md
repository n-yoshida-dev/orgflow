# ADR Index

このディレクトリは、OrgFlow の設計判断を記録した ADR（Architecture Decision Record）の一覧です。
各 ADR には、採用した案、不採用にした案、受け入れる制約、見直す条件を残します。

各 ADR の冒頭には `> **決定**:` の一文があります。まずそこだけ読めば判断の結論が分かります。

## 一覧

| # | 決定 | Status | 日付 | 最終追記 |
|---|---|---|---|---|
| [001](./ADR-001-なぜ題材をタスク管理ではなく申請・承認にしたか.md) | 題材として申請・承認フローを持つ業務アプリを採用する | Accepted | 2026-03-19 | - |
| [002](./ADR-002-監査ログを独立した概念として扱う.md) | 監査ログ（audit_log）を独立した概念として設計する | Accepted | 2026-03-19 | - |
| [003](./ADR-003-RBACの最小方針.md) | 組織への所属に role を付与し、role は操作権限のみを表す | Accepted | 2026-03-19 | 2026-03-25 |
| [004](./ADR-004-業務フローの状態遷移をどう表現するか.md) | request の状態を `request_status` と遷移ルールで明示的に表現する | Accepted | 2026-03-19 | - |
| [005](./ADR-005-requestの申請者・組織の参照方式.md) | request は申請者 user と internal organization を直接持つ | Accepted | 2026-03-24 | 2026-04-08 |
| [006](./ADR-006-applied_approval_routeの保存構造.md) | ~~applied_approval_route を3層構造で保存する~~ → 009 と同一結論のため 009 を読む | **Superseded by 009** | 2026-03-24 | - |
| [007](./ADR-007-requestの版管理方式.md) | request を「申請版」の単位とし、系列と版番号で識別する | Accepted | 2026-03-24 | - |
| [008](./ADR-008-承認ルートの適用結果をrequestに紐づけて保持する.md) | submit 時に評価した承認ルートを request に紐づけて保存する | Accepted | 2026-03-24 | - |
| [009](./ADR-009-applied_approval_routeを親子構造で保持する.md) | 承認ルートの適用結果を親子3層で保持し、1段に複数承認者を許す | Accepted | 2026-03-24 | - |
| [010](./ADR-010-同一承認段に複数承認者がいる場合のルール.md) | 同一承認段は誰か1人の承認で完了する | Accepted | 2026-03-24 | - |
| [011](./ADR-011-audit_logの操作対象参照方式.md) | audit_log の操作対象は `target_type` / `target_id` で持ち FK は置かない | Accepted | 2026-03-25 | - |
| [012](./ADR-012-approval_policyとapproval_routeの参照方向.md) | `approval_policy` が `approval_route_id` を外部キーとして持つ | Accepted | 2026-03-25 | - |
| [013](./ADR-013-tenantとinternal_organizationを分離する.md) | tenant と internal organization を別概念として分離する | Accepted | 2026-04-06 | - |
| [014](./ADR-014-tenantスコープ権限とinternal_organizationスコープ権限を分離する.md) | 権限を `TENANT_ROLE` と `INTERNAL_ORGANIZATION_ROLE` に分離する | Accepted | 2026-04-06 | 2026-04-10 |
| [015](./ADR-015-shared-db前提でtenant_idにより分離する.md) | shared DB 前提とし、tenant 境界を `tenant_id` で表現する | Accepted | 2026-04-07 | 2026-04-08 |
| [016](./ADR-016-認証方式の選定理由.md) | Bearer token（JWT / HS256）を用いる認証済み API として設計する | Accepted | 2026-04-19 | 2026-06-19 |
| [017](./ADR-017-OpenAPIをAPI契約としてv1に絞って先に管理する理由.md) | OpenAPI を API 契約として v1 の重要 endpoint から先に管理する | Accepted | 2026-04-19 | - |
| [018](./ADR-018-ローカル開発DBはDocker ComposeのPostgreSQLで起動する.md) | ローカル開発DBを Docker Compose の PostgreSQL で起動する | Accepted | 2026-04-29 | - |
| [019](./ADR-019-DBマイグレーションとseedデータをFlywayで管理する.md) | DBマイグレーションと seed データを Flyway で管理する | Accepted | 2026-05-03 | 2026-06-04 |

## テーマ別に読む

- 題材・全体方針: 001
- 業務フローと request: 004 / 005 / 007 / 008
- 承認ルート: 009 / 010 / 012（006 は 009 に置き換え済み）
- テナントと権限: 003 / 013 / 014 / 015
- 認証・API 契約: 016 / 017
- 監査ログ: 002 / 011
- ローカル開発基盤: 018 / 019

## テンプレート

新しい ADR を作成するときは [ひな形](./ひな形-ADR-xxx-___.md) を使ってください。
