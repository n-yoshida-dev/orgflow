# ADR Index

このディレクトリは、OrgFlow の設計判断を記録した ADR（Architecture Decision Record）の一覧です。
各 ADR には、採用した案、不採用にした案、受け入れる制約、見直す条件を残します。

各 ADR の冒頭には `> **決定**:` の一文があります。まずそこだけ読めば判断の結論が分かります。

1 ファイルには 1 つの決定だけを書きます。決定が増えた場合は既存 ADR へ追記せず、新しい ADR を作成して両方の「関連」に相互リンクを置きます。

## 一覧

| # | 決定 | Status | 日付 | 最終追記 |
|---|---|---|---|---|
| [001](./ADR-001-なぜ題材をタスク管理ではなく申請・承認にしたか.md) | 題材として申請・承認フローを持つ業務アプリを採用する | Accepted | 2026-03-19 | - |
| [002](./ADR-002-監査ログを独立した概念として扱う.md) | 監査ログ（audit_log）を独立した概念として設計する | Accepted | 2026-03-19 | - |
| [003](./ADR-003-roleは所属に付与し操作権限のみを表す.md) | role は組織への所属に付与し、操作権限のみを表す | Accepted | 2026-03-19 | - |
| [004](./ADR-004-業務フローの状態遷移をどう表現するか.md) | request の状態を `request_status` と遷移ルールで明示的に表現する | Accepted | 2026-03-19 | - |
| [005](./ADR-005-requestは申請者と組織を直接参照する.md) | request は申請者 user と internal organization を直接参照する | Accepted | 2026-03-24 | - |
| [006](./ADR-006-applied_approval_routeの保存構造.md) | ~~applied_approval_route を3層構造で保存する~~ → 009 と同一結論のため 009 を読む | **Superseded by 009** | 2026-03-24 | - |
| [007](./ADR-007-requestの版管理方式.md) | request を「申請版」の単位とし、系列と版番号で識別する | Accepted | 2026-03-24 | - |
| [008](./ADR-008-承認ルートの適用結果をrequestに紐づけて保持する.md) | submit 時に評価した承認ルートを request に紐づけて保存する | Accepted | 2026-03-24 | - |
| [009](./ADR-009-applied_approval_routeを親子構造で保持する.md) | 承認ルートの適用結果を親子3層で保持し、1段に複数承認者を許す | Accepted | 2026-03-24 | - |
| [010](./ADR-010-同一承認段に複数承認者がいる場合のルール.md) | 同一承認段は誰か1人の承認で完了する | Accepted | 2026-03-24 | - |
| [011](./ADR-011-audit_logの操作対象参照方式.md) | audit_log の操作対象は `target_type` / `target_id` で持ち FK は置かない | Accepted | 2026-03-25 | - |
| [012](./ADR-012-approval_policyとapproval_routeの参照方向.md) | `approval_policy` が `approval_route_id` を外部キーとして持つ | Accepted | 2026-03-25 | - |
| [013](./ADR-013-tenantとinternal_organizationを分離する.md) | tenant と internal organization を別概念として分離する | Accepted | 2026-04-06 | - |
| [014](./ADR-014-権限をtenantスコープとinternal_organizationスコープに分離する.md) | 権限を `TENANT_ROLE` と `INTERNAL_ORGANIZATION_ROLE` に分離する | Accepted | 2026-04-06 | - |
| [015](./ADR-015-shared-db前提でtenant_idにより分離する.md) | shared DB 前提とし、tenant 境界を `tenant_id` で表現する | Accepted | 2026-04-07 | - |
| [016](./ADR-016-認証はBearerTokenで行う.md) | 認証は Bearer token（JWT / HS256）で行う | Accepted | 2026-04-19 | - |
| [017](./ADR-017-OpenAPIをAPI契約としてv1に絞って先に管理する理由.md) | OpenAPI を API 契約として v1 の重要 endpoint から先に管理する | Accepted | 2026-04-19 | - |
| [018](./ADR-018-ローカル開発DBはDocker ComposeのPostgreSQLで起動する.md) | ローカル開発DBを Docker Compose の PostgreSQL で起動する | Accepted | 2026-04-29 | - |
| [019](./ADR-019-DBマイグレーションとseedデータをFlywayで管理する.md) | DBマイグレーションと seed データを Flyway で管理する | Accepted | 2026-05-03 | - |
| [020](./ADR-020-APIをstateless前提のSecurity構成にする.md) | API を stateless 前提の Spring Security 構成にする | Accepted | 2026-04-29 | - |
| [021](./ADR-021-ログイン認証はDBのusersとBCryptで照合する.md) | ログイン認証は DB の users と BCrypt で照合する | Accepted | 2026-05-09 | - |
| [022](./ADR-022-JWTの発行と検証をSpringSecurityに寄せる.md) | JWT の発行と検証を Spring Security の標準機構に寄せる | Accepted | 2026-05-12 | - |
| [023](./ADR-023-認証済みuserIdはJWTのsubから取り出す.md) | 認証済み userId は JWT の `sub` から取り出し、Service へは UUID で渡す | Accepted | 2026-05-24 | - |
| [024](./ADR-024-tenant選択状態をJWTのcurrent_tenant_idで表現する.md) | tenant 選択状態を JWT の `current_tenant_id` claim で表現する | Accepted | 2026-06-04 | - |
| [025](./ADR-025-組織内roleは中間テーブルで付与する.md) | 組織内 role は所属との中間テーブルで付与する | Accepted | 2026-03-25 | - |
| [026](./ADR-026-申請先に選べる組織をrequesterRoleを持つ所属先に限る.md) | 申請先に選べる組織を、requester role を持つ所属先に限る | Accepted | 2026-04-08 | - |
| [027](./ADR-027-管理系操作をtenant管理者とinternal_organization管理者に割り当てる.md) | 管理系操作を tenant 管理者と internal organization 管理者に割り当てる | Accepted | 2026-04-10 | - |
| [028](./ADR-028-tenant_membershipsは組み合わせを一意とする.md) | `tenant_memberships` は `(tenant_id, user_id)` を一意とする | Accepted | 2026-06-04 | - |
| [029](./ADR-029-seedのpasswordはprefix付きハッシュで保存する.md) | seed の password は `{bcrypt}` prefix 付きハッシュで保存する | Accepted | 2026-05-03 | - |

番号は登録順の通し番号であり、日付順とは一致しません。020 以降は既存 ADR から分割した決定を含むため、019 より前の日付が混ざります。

## テーマ別に読む

- 題材・全体方針: 001
- 業務フローと request: 004 / 005 / 026 / 007 / 008
- 承認ルート: 009 / 010 / 012（006 は 009 に置き換え済み）
- テナントと権限: 013 / 015 / 003 / 025 / 014 / 027 / 028
- 認証: 016 → 020 → 021 → 022 → 023 → 024（この順に読むと実装の進み方に沿う）
- API 契約: 017
- 監査ログ: 002 / 011
- ローカル開発基盤: 018 / 019 / 029

## テンプレート

新しい ADR を作成するときは [ひな形](./ひな形-ADR-xxx-___.md) を使ってください。
