## request_type

- Phase 1 では `request_type` はマスタテーブル参照にせず、`text + CHECK` で扱う
- 理由は、現時点では request_type 管理UI、有効/無効管理、表示順管理まで広げないため
- `requests.request_type` と `approval_policies.request_type` は同じ候補値を使う
- 将来的に次が必要になった場合は、`request_types` テーブル化を再検討する
  - 管理UI
  - 表示順
  - 有効/無効フラグ
  - organization ごとの差分管理

## tenant role を別に持つ理由

`ROLE` だけで権限を表すと、tenant 全体に対する管理系権限と、internal organization 単位の業務操作権限が混ざる。

そのため、OrgFlow では権限を次の 2 つに分ける。

- `TENANT_ROLE`
  tenant 全体に対する管理系権限

- `ROLE`
  internal organization 単位の業務操作権限

`TENANT_MEMBERSHIP_ROLES` は、tenant_membership に対して tenant_role が付与されている事実を表す。
`MEMBERSHIP_ROLES` は、internal_organization_membership に対して role が付与されている事実を表す。

この分離により、tenant 切替、tenant 全体管理、tenant 内部の部門権限を別レイヤーで説明できるようにする。

## physical ER で tenant 整合性を保証する方針

`internal_organization_memberships` は、tenant_membership と internal_organization の両方を参照する。

論理ER図では `tenant_membership_id` と `internal_organization_id` を持つが、
physical ER では tenant 一致を DB 制約で保証する前提とする。

そのため、physical ER では必要に応じて child 側に `tenant_id` を持たせ、
複合FKにより次を保証する。

- internal_organization_memberships の tenant_membership は、同じ tenant の membership であること
- internal_organization_memberships の internal_organization は、同じ tenant に属すること

## approval_policy.min_amount の Phase 1 方針

Phase 1 では `min_amount = 0` を「金額条件なし」とみなす。  
`NULL` による条件なし表現は採らない。

理由:

- unique 制約の扱いを単純化しやすい
- Phase 1 では業務ルールの説明可能性を優先するため

## approver の簡略化

Phase 1 では、共通ルート定義側・適用結果側ともに approver は `user_id` の直参照で持つ。  
tenant 所属や internal organization 所属の整合性は、DB ではなくアプリケーション側の業務ルールでチェックする。
