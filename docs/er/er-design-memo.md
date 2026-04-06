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
