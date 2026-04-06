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

## 今回の物理ER図で確定したこと

### users の持ち方

- `users` は業務上の利用者そのものを表す
- Phase 1 では、認証情報を別テーブルへ分離せず、`users` に次を持つ
  - `display_name`
  - `login_id`
  - `hashed_password`
  - `mail_address`
- `id` は内部主キーであり、業務上の表示名やログインIDとは役割を分ける

### request_status の確定

- Phase 1 の request_status は次の 6 つで確定した
  - `draft`
  - `in_progress`
  - `approved`
  - `returned`
  - `rejected`
  - `cancelled`
- `submitted` は独立状態にせず、申請後は `in_progress` で表現する
- 差し戻し後の再申請では、既存 request を更新せず、新しい request を作る
- 旧版 request は `returned` のまま残す

### request.amount の扱い

- `amount` は `NOT NULL` とし、DB制約では `amount >= 0` を許容する
- ただし、0 円を許容するのは `draft` のみという業務ルールを別途持つ
- `draft` から `in_progress` へ遷移する時点では、0 円申請を不正として扱う
- この検証はフロントエンドだけでなく、バックエンド側でも行う前提とする

### request_type の扱い

- Phase 1 では `request_type` はマスタテーブル参照にせず、`text + CHECK` で持つ
- `requests.request_type` と `approval_policies.request_type` は同じ候補値を使う
- 将来的に管理UIや有効/無効管理が必要になった場合は、`request_types` テーブル化を見直す余地を残す
- ただし、Phase 1 の時点では `request_types` テーブルは採用しない

### 承認ルート定義と適用結果の分離

- テンプレート側には次を持つ
  - `approval_routes`
  - `approval_route_steps`
  - `approval_route_step_approvers`
- request 作成時の確定結果側には次を持つ
  - `applied_approval_routes`
  - `applied_approval_route_steps`
  - `applied_approval_route_step_approvers`
- この分離により、承認ルート定義が後から変更されても、既存 request に適用済みの承認ルートは変わらない

### audit_logs の保持方針

- audit_log は approval の代替ではなく、横断的な監査記録として独立して持つ
- 操作対象は `target_type` と `target_id` で保持し、`target_id` には外部キーを置かない
- 一方で、操作主体は次を両方持つ
  - 機械的追跡のための ID
    - `user_id`
    - `actor_organization_id`
  - 監査画面可読性のための表示用スナップショット
    - `actor_user_display_name`
    - `actor_user_mail_address`
    - `actor_organization_name`
- これにより、「後から追えること」と「人が監査画面で読めること」を両立する

### インデックス設計で学んだこと

- 主キーや一意制約は、意味だけでなく自動インデックス生成も伴う
- FK だから無条件に index を付けるのではなく、
  「その列でどんな検索を行うか」で判断する
- `requests` では、少なくとも次を採用した
  - `applicant_user_id`
  - `organization_id`
  - `(organization_id, status)`
  - `(request_series_id, version_no) [unique]`

## 今回、新たに残った未確定論点

- approval_policy の複数候補が同時にマッチした場合の優先順位
- users / organizations の物理削除戦略
- audit_logs に対する親側削除方針と FK の見直し条件
- request_type を将来マスタテーブル化するかどうか
- 外部 IdP 導入時に users と認証情報を分離するかどうか
