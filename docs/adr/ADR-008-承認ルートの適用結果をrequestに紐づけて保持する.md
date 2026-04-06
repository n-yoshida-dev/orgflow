# ADR-008: 承認ルートの適用結果をrequestに紐づけて保持する

## Context

承認ルートは、organizationごとに定義されるルール（approval_policy）から決定される。
しかし、申請後にルールが変更された場合でも、過去申請時点の承認ルートは保持される必要がある。

## Decision

- request は approval_policy を直接参照しない
- request 作成時に承認ルートを評価し、その結果を `applied_approval_route` として保存する
- `applied_approval_routes` は `request_id` を外部キーとして持ち、1 request に対して 1 applied_approval_route とする

## Consequences

### Positive

- 過去申請の承認ルートが後から変わらない
- 監査・説明責任に対応できる
- 承認処理を request 単位で完結できる
- `applied_approval_routes.request_id` に unique 制約を置くことで、1 request に対して 1 applied_approval_route であることを DB 制約として表現できる

### Negative

- 同じルート定義が重複保存される可能性がある
- データ量が増加する

## Alternatives Considered

### 1. approval_policy を直接参照する

- 不採用理由
  - ルール変更時に過去申請の意味が変わる

### 2. request が `applied_approval_route_id` を直接持つ

- 不採用理由
  - 現在の設計では、`applied_approval_routes` を request に従属する適用結果として扱っており、
    `applied_approval_routes.request_id` を unique で持つ方が、関係の向きと業務上の意味が一致する
