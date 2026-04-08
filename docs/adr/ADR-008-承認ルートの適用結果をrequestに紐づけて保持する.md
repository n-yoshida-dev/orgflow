# ADR-008: 承認ルートの適用結果を request に紐づけて保持する

- Status: Accepted
- Date: 2026-03-24

## 背景

- request 作成時には、approval_policy を評価して、その申請に対してどの承認ルートを使うかを確定する必要がある
- 申請後に approval_policy や approval_route が変更されても、過去 request の承認ルートは変わってはならない
- ルール定義そのものを直接参照し続けると、過去 request の意味が後から変わる危険がある
- request ごとに「申請時点で確定した承認ルート」を保持する構造が必要だった

## 採用した案

request は approval_policy を直接参照しない。

- request 作成時に承認ルートを評価する
- その結果を `applied_approval_route` として保存する
- `applied_approval_routes` は `request_id` を外部キーとして持つ
- `applied_approval_routes.request_id` には unique 制約を置き、1 request に対して 1 applied_approval_route とする

## 採用理由

- 過去 request の承認ルートが、後からルール変更の影響を受けなくなる
- 「この request には、申請時点でこのルートが適用された」と説明できる
- 承認処理を request 単位で完結させやすい
- 1対1関係を `applied_approval_routes.request_id` の unique 制約で明示できるため、従属関係とDB制約の向きが一致する

## 不採用案

### 案A: request が approval_policy を直接参照する

- approval_policy の変更時に、過去 request の意味が変わってしまう
- 監査や説明責任の観点で弱い

### 案B: request が `applied_approval_route_id` を直接持つ

- 今回は `applied_approval_route` を request に従属する適用結果として扱っている
- `applied_approval_routes.request_id` を unique で持つ方が、存在の向きと業務上の意味が自然である

## 受け入れる制約

- 同じルート定義が request ごとに重複保存される可能性がある
- 保存対象が増えるため、データ量は増える
- approval_policy と applied_approval_route の違いを文書で明確に残さないと混同されやすい

## 見直す条件

- applied_approval_route の重複保存コストが無視できなくなった場合
- request ごとの確定結果を別方式で保持した方が、追跡性と実装の両面で有利になった場合
- 承認ルート適用ロジックが複雑化し、1 request = 1 applied_approval_route の前提を見直す必要が生じた場合
