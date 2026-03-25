# ADR-012: approval_policy と approval_route の参照方向

- Status: Accepted
- Date: 2026-03-25

## 背景

- `approval_policy`（承認ルートを決めるルール）と
  `approval_route`（承認ルートのテンプレート）を別概念として分けた
- この2つをどちらが参照するかを決める必要があった
- 複数の `approval_policy` が同じ `approval_route` を参照しうるケースが想定された
  （例: 申請種別「交通費」と「資格受験費」が同じ承認ルートを使う）

## 採用した案

- `approval_policy` が `approval_route_id` を外部キーとして持つ

## 採用理由

- 複数の `approval_policy` が共通の `approval_route` を参照できる
- `approval_route` 側に `approval_policy_id` を持つと、
  1つのルートに1つのポリシーしか紐づけられない構造になる
- ポリシーがルートを選ぶ、という業務上の意味と FK の方向が一致する

## 不採用案

- `approval_route` が `approval_policy_id` を持つ案:
  1つのルートを複数のポリシーで共有できなくなる

## 受け入れる制約

- `approval_route` は `approval_policy` の存在を知らないため、
  どのポリシーから参照されているかを `approval_route` 単体では把握できない

## 見直す条件

- 1つのポリシーに対して条件分岐で複数のルートを割り当てる必要が生じた場合
