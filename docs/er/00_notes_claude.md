# ER図設計メモ

## このファイルの目的

ER図設計で決めたこと、保留にしたこと、後で見直すべき判断を記録する。
詳細な判断理由は ADR に書く。このファイルはその索引と補足メモとして使う。

---

## 概念ER図で確定した内容

- `organization_membership` は `user` と `organization` の所属関係を表す中間テーブルである。
  1件 = 1人のユーザーが1つの組織に所属しているという事実。
- `role` は `user` に直接付与せず、`organization_membership` に対して付与する。
  同じユーザーでも組織ごとに権限が異なる業務ルールを表現するため。
- `audit_log` は通常の親子関係として概念ER図に線を引かない。
  `request` や `approval` など複数の概念を横断して操作を記録する独立した記録であるため。
- `applied_approval_route` は `request` 作成時に `approval_policy` を評価して確定した承認ルートの保存結果である。
  `approval_policy` 自体への参照は持たない（後述）。

---

## 論理ER図で確定した内容

### request の識別と版管理

- `request` の主キーは `id`（UUID）とする。
- 申請系列内での版の一意性は `(request_series_id, version_no)` の複合一意制約で表現する。
- `request` には `status`（申請中 / 承認済み / 差し戻し / 却下 / 取消）と、
  現在承認段を示す `current_applied_approval_route_step_id` を持つ。
  承認完了・却下・取消など「現在段が存在しない状態」では NULL を許容する。

### request の参照先

- `request` は `applicant_user_id`（申請者）と `organization_id`（所属組織）を直接持つ。
  `organization_membership_id` は持たない。
  理由は ADR-005 を参照。

### approval の構造

- `approval` の1件は「ある申請版のある承認段に対して行われた1回の判断」を表す。
- `applied_approval_route_step_id` を外部キーとして持つことで、
  どの承認段に対する判断かをDBレベルで表現する。
- `(request_id, applied_approval_route_step_id)` に一意制約を置く。
  同一承認段に対する重複判断をDB制約で防ぐため。

### approval_policy と approval_route の関係

- `approval_policy` は、ある組織において、ある申請種別・金額条件に対して
  どの承認ルートを適用するかを決めるルールの1件である。
- `approval_route` は承認ルートのテンプレートである。
  複数の `approval_policy` が同じ `approval_route` を参照しうるため、
  `approval_policy` が `approval_route_id` を持つ方向とした。
- `approval_policy.min_amount` は閾値であり、NULL の場合は金額条件なしを意味する。

### applied_approval_route の構造

- `applied_approval_routes`（親）→ `applied_approval_route_steps`（段）
  → `applied_approval_route_step_approvers`（段ごとの承認者）の3層構造で保存する。
- `applied_approval_routes` は `approval_policy_id` を持つ。
  申請時にどのポリシーが評価されてこのルートになったかを後から確認できるようにするため。
- 後から `approval_policy` が変更されても、この保存結果は変わらない。

### audit_log の参照方式

- `audit_log` は `target_type`（VARCHAR: 操作対象の種別名、例: "request" / "user"）と
  `target_id`（UUID: 操作対象の識別子）を持つ。
- `target_id` に対する外部キー制約は置かない。
  監査ログは操作時点の不変な証跡であり、参照先が削除されても記録を消してはいけないため。

### organization_membership と role の多重度

- 1つの `organization_membership` に対して複数の `role` が付きうる。
- `membership_roles` テーブルを中間テーブルとして持ち、
  `(organization_membership_id, role_id)` に一意制約を置く。

---

## 保留・未確定の論点

- `approval_policy` の評価で複数候補がマッチした場合、`min_amount` が最大のものを採用するルールとしているが、
  このロジックはアプリコードで実装する。DB設計には直接影響しない。
- `approval_policy.request_type` と `requests.request_type` はともに VARCHAR の自由文字列である。
  表記ゆれによるポリシーマッチ失敗のリスクがある。
  将来的には enum または参照テーブルで統一する必要がある。
- `applied_approval_routes` が参照する `approval_policy` が後から変更された場合の扱いは未確定。
  ADR-006 の「見直す条件」として記録済み。
- `approval_route_step_approvers` は現在 `user_id` で承認者を指定している。
  人事異動・担当変更のたびにデータ変更が必要になる点は既知のトレードオフとして受け入れる。
  将来的に `organization_membership` と役割の組み合わせで承認者を動的選定する案は Phase 1 の対象外。
- 上位者が部下の過去申請を閲覧できる条件は未確定。
- 代理承認・代理申請は Phase 1 の対象外。
