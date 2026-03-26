# ADR-003: RBACの最小方針

- Status: Accepted
- Date: 2026-03-19

## 背景

- organization / role / request / approval / audit_log を扱う構成
- 以下を混同すると設計が崩れる
  - 役職
  - 承認ルート
  - 操作権限
- ユーザー単位ではなく、組織との関係で権限を管理したい

## 採用した案

組織への所属に対してroleを付与し、roleは操作権限のみを表す

## 採用理由

- 同一ユーザーでも組織ごとに権限を変えられる
- 承認ロジックと操作権限を分離できる
- approval_policyに業務ルールを集約できる

## 不採用案

- 案A: 役職をそのまま認可に使用
  異動・兼務・例外対応に弱い
- 案B: roleに承認条件を含める
  概念が肥大化し、責務が不明確になる

## 受け入れる制約

- 概念数が増える
- 初学者にとって理解コストが上がる

## 見直す条件

- 代理承認や閲覧権限などを追加する場合
- roleとapproval_policyの責務分担を再整理する必要が出た場合

## 追記: membership_roles テーブルの追加（2026-03-25）

### 決定

`role` は `organization_memberships` に直接持たず、
`membership_roles` という中間テーブルを介して付与する。

`membership_roles` の1件は、
「ある組織への所属（organization_membership）に対して、あるロールが付与されている」
という事実を表す。

`(organization_membership_id, role_id)` の組み合わせに一意制約を置き、
同じ所属に対して同じロールが重複して付与されることを防ぐ。

### 採用理由

同じ組織への所属に対して複数のロールが付きうるため、
`organization_memberships` テーブルに `role_id` カラムを直接持つと
1ロールしか表現できない。

### 受け入れる制約

あるユーザーの組織内ロールを確認するクエリで、
`organization_memberships` と `membership_roles` の JOIN が必要になる。
