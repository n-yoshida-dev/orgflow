# ADR-009: applied_approval_routeを親子構造で保持する

## Context

承認ルートは複数の承認段（step）で構成される。
また、1つの承認段に複数の承認者が存在する可能性がある。

## Decision

- 承認ルートは以下の3層で表現する
  - applied_approval_route（ルート全体）
  - applied_approval_route_step（承認段）
  - applied_approval_route_step_approver（承認者）

- 1つの承認段に複数承認者を持てる構造とする

## Consequences

### Positive

- 承認段と承認者の粒度を分離できる
- 将来の複雑な承認条件に対応しやすい
- 正規化された構造になる

### Negative

- テーブル数が増える
- クエリが複雑になる

## Alternatives Considered

### 1. 1テーブルで保持する

- 不採用理由
  - 承認段と承認者の粒度が混在する

### 2. 1段1承認者に制限する

- 不採用理由
  - 実務ユースケースに対応できない
