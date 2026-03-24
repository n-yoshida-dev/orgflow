# ADR-008: 承認ルートの適用結果をrequestに紐づけて保持する

## Context

承認ルートは、organizationごとに定義されるルール（approval_policy）から決定される。
しかし、申請後にルールが変更された場合でも、過去申請時点の承認ルートは保持される必要がある。

## Decision

- requestはapproval_policyを直接参照しない
- request作成時に承認ルートを評価し、その結果を `applied_approval_route` として保存する
- requestは `applied_approval_route_id` を参照する

## Consequences

### Positive

- 過去申請の承認ルートが後から変わらない
- 監査・説明責任に対応できる
- 承認処理をrequest単位で完結できる

### Negative

- 同じルート定義が重複保存される可能性がある
- データ量が増加する

## Alternatives Considered

### 1. approval_policyを直接参照する

- 不採用理由
  - ルール変更時に過去申請の意味が変わる

### 2. 共通ルート定義のみ参照する

- 不採用理由
  - 同様に過去時点の状態を保持できない
