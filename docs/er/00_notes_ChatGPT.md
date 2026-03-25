# ERメモ

## 位置づけ

このファイルは、概念ER図・論理ER図・物理ER図をまたいで、途中で決めた判断や、次の段階へ送る未確定論点を残すためのメモである。  
完成済みの正本は ADR、業務ルールは `domain-rules.md`、概念の役割は `design-overview.md` に寄せる。  
ここでは、ER設計を進める途中で判断したことと、まだ確定していないことを分けて残す。

## 今回の論理ER図で確定したこと

### request の識別と版管理

- `request` は「1つの申請系列のうち、ある1版の申請データ」を表す
- `request` の主キーは `id` とする
- 申請系列は `request_series_id`、版は `version_no` で表す
- `(request_series_id, version_no)` は一意制約で重複を防ぐ
- 差し戻し後の再申請では既存 request を更新せず、新しい request を作る

### request の申請者と組織の持ち方

- `request` は `organization_membership_id` を持たない
- `request` は `applicant_user_id` と `organization_id` を直接持つ
- 理由は、申請時点の申請者と組織の帰属を安定して保持するためである

### role の付与単位

- `role` は `user` に直接付与しない
- `organization_membership` を `user` と `organization` の所属関係として独立させる
- `organization_membership` と `role` は多対多とし、`membership_roles` を中間テーブルとして持つ
- `(user_id, organization_id)` は `organization_memberships` で一意とする
- `(organization_membership_id, role_id)` は `membership_roles` で一意とする

### approval と current step の持ち方

- `approval` は `request` に対する1回の判断履歴である
- `approval` は `applied_approval_route_steps.id` を参照し、どの承認段への判断かを表す
- `request` は `current_applied_approval_route_step_id` を持ち、現在どの承認段にいるかを表す
- `step_no` は表示順や段番号の説明に使い、参照の正本には使わない

### applied_approval_route の持ち方

- `applied_approval_route` は request 作成時に確定した承認ルートの保存結果である
- `applied_approval_routes` は `request_id` を unique で持ち、1 request に対して 1 applied_approval_route とする
- `applied_approval_route` は親、`applied_approval_route_steps` は子、`applied_approval_route_step_approvers` は孫の関係で持つ
- `(applied_approval_route_id, step_no)` は一意とする
- `(user_id, applied_approval_route_step_id)` は一意とする

### approval_policy と approval_route の分離

- `approval_policy` は「どの条件の request にどの承認ルートを適用するか」を決めるルールとする
- `approval_route` は共通の承認ルート定義とする
- `approval_policy` は `approval_route_id` を持ち、条件に一致したときにどのルートを使うかを表す
- `approval_route` は `approval_route_steps` と `approval_route_step_approvers` を持つ
- `(approval_route_id, step_no)` は一意とする
- `(user_id, approval_route_step_id)` は一意とする

### approval_policy の現時点の条件

- `approval_policy` は `organization_id` に属する
- `approval_policy` は `request_type` を条件に含む
- `approval_policy.min_amount` は金額条件の下限を表す
- `min_amount` が `NULL` の場合は、金額条件なしとみなす

### 同一承認段の複数承認者ルール

- 同一承認段に複数承認者を置くことは認める
- ただし、Phase 1 では「誰か1人が承認した時点でその段は完了する」とする
- そのため、`approvals` には `(request_id, applied_approval_route_step_id)` の一意制約を置く

### audit_log の持ち方

- `audit_log` は独立した横断記録とする
- `audit_logs` は `target_type` と `target_id` を持ち、対象種別と対象識別子を分けて表す
- `operation_type` は enum として扱う
- `occurred_at` を持ち、操作時刻を表す

## 今回意図的に単純化したこと

### 承認者を共通ルート定義側でも user_id で持つ

- `approval_route_step_approvers` は、現時点では具体的な `user_id` を持つ
- 将来的には role や organization_membership を使って承認者条件を表した方が柔軟だが、Phase 1 ではそこまで広げない

### 全員承認は直接表現しない

- Phase 1 では「同一承認段で全員承認が必要」というルールを直接表現しない
- 全員承認が必要な場合は、承認段を分けて順番に承認させる前提とする

## まだ確定していないこと

### approval_policy の評価順序

- 複数の `approval_policy` が候補になった場合に、どれを採用するかの評価ルールは未確定
- たとえば、`organization_id` と `request_type` が一致し、`amount >= min_amount` の候補のうち、`min_amount` が最大のものを採用する、という案がある
- ただし、現時点では文章で固定しただけで、DB制約としてはまだ表現していない

### request.amount の物理制約

- 経費申請アプリとしては `request.amount` は必須にしたい
- ただし、論理ER図では他申請種別へ広がる可能性も見て `NULL` を許容している
- 物理ER図では、Phase 1 の対象を経費申請に限定するなら `NOT NULL` に寄せるかを再確認する

### approval_policy の変更履歴

- `applied_approval_routes` には `approval_policy_id` を保持している
- これは生成元の policy を追跡するための出所情報として扱う
- ただし、後から policy 自体が編集されたときに、どこまで過去時点の意味を保証したいかは未確定

### 代理申請・代理承認

- Phase 1 には含めない前提で進めている
- 将来的に導入する場合、`request` の申請者と実行者、`approval` の判断者と代理元の区別が必要になる

### 閲覧権限の細分化

- 申請者本人、承認者、管理者以外に、上位者や同組織管理者がどこまで過去申請を閲覧できるかは未確定
- これは OpenAPI と認可設計の段階で詰める
