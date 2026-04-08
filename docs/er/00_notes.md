# ER 設計メモ

## 位置づけ

このファイルは、ER 図を読むために補足が必要な事項と、まだ固定しきれていない論点だけを置くためのメモです。

- 設計理由は `docs/adr/` に残す
- 業務ルールは `docs/domain-rules.md` に残す
- 概念の責務分担は `docs/design-overview.md` に残す

このファイルは、ER 図の補助文書であり、独立した設計概要にはしません。

## 現在の ER 前提

### request の扱い

- `request` は申請系列全体ではなく、1 件の申請版を表す
- 差し戻し後の再申請では既存 `request` を更新せず、新しい `request` を作る
- 系列は `request_series_id`、版は `version_no` で表す

### request の参照先

- `request` は `applicant_user_id` と `internal_organization_id` を直接持つ
- `request` は申請時点の不変な事実を保持するため、現在の所属関係そのものは参照しない
- tenant 境界は `tenant_id` と複合参照で補助的に表現する

### role と tenant_role の分離

- tenant 全体に対する管理権限は `tenant_role`
- internal organization 単位の業務権限は `role`
- 付与関係はそれぞれ `tenant_membership_roles` と `membership_roles` で表す

### approval の扱い

- `approval` の 1 件は、ある申請版のある承認段に対する 1 回の判断を表す
- 現在どの承認段を処理中かは `request.current_applied_approval_route_step_id` で表す
- `approval` は現在状態そのものではなく、判断履歴として扱う

### 承認ルートの扱い

- `approval_policy` は承認ルートを決めるルール
- `approval_route` は承認ルートのテンプレート
- `applied_approval_route` は申請時に確定した適用結果
- 適用結果は `applied_approval_routes` → `applied_approval_route_steps` → `applied_approval_route_step_approvers` の 3 層で保持する

### audit_log の扱い

- `audit_log` は横断的な監査記録として扱う
- 操作対象は `target_type` と `target_id` で保持する
- 操作対象側への外部キー制約は置かない

## Phase 1 の簡略化ポイント

### request_type

- Phase 1 では `request_type` はマスタテーブル化しない
- `text + CHECK` で扱う
- 管理 UI、表示順、有効/無効管理が必要になったら `request_types` テーブル化を再検討する

### approver の表現

- Phase 1 では、承認者は `user_id` の直参照で持つ
- 人事異動や担当変更に弱いことは既知のトレードオフとして受け入れる
- 所属や役割からの動的選定は Phase 1 の対象外とする

## 未確定の論点

- 上位者や同組織管理者がどこまで過去申請を閲覧できるか
- 代理申請・代理承認を今後どう扱うか
- 承認者を `user_id` 直参照から動的選定へ切り替えるか
- `request_type` を将来的に参照テーブル化するか

## 使い方

- このファイルに新しい結論を書き足す前に、ADR に上げるべき判断かどうかを先に確認する
- すでに ADR / `design-overview.md` / `domain-rules.md` / ER 図本体へ反映済みの内容は、このファイルに重複して残さない
- 役割を終えたメモは消す
