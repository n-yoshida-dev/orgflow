# ADR-008: 承認ルートの適用結果を request に紐づけて保持する

- Status: Accepted
- Date: 2026-03-24

> **決定**: request は approval_policy を直接参照しない。submit 時に承認ルートを評価し、その結果を applied_approval_route として保存する（1 request に 1 applied_approval_route）。

## 背景

- request は draft と submitted を分けて扱う
- draft 作成時点では、申請内容がまだ確定しておらず、承認ルートを固定しない
- submit 時には、approval_policy を評価して、その申請に対してどの承認ルートを使うかを確定する必要がある
- 申請後に approval_policy や approval_route が変更されても、過去 request の承認ルートは変わってはならない
- ルール定義そのものを直接参照し続けると、過去 request の意味が後から変わる危険がある
- request ごとに「申請時点で確定した承認ルート」を保持する構造が必要だった

## 採用した案

request は approval_policy を直接参照しない。

- draft 作成時には承認ルートを確定しない
- submit 時に承認ルートを評価する
- その結果を `applied_approval_route` として保存する
- `applied_approval_routes` は `request_id` を外部キーとして持つ
- `applied_approval_routes.request_id` には unique 制約を置き、1 request に対して 1 applied_approval_route とする

## 採用理由

- draft の編集中に承認ルートを早く固定しすぎずに済む
- 「この request には、submit 時点でこのルートが適用された」と説明できる
- approval_policy や approval_route の後続変更が、提出済み request の意味に影響しない
- 承認処理を request 単位で完結させやすい
- 1対1関係を `applied_approval_routes.request_id` の unique 制約で明示できるため、従属関係とDB制約の向きが一致する

## 不採用案

### 案A: draft 作成時に承認ルートを確定する

- draft は未確定要素が多く、編集中の変更で承認ルート再評価が必要になりやすい
- create と submit の責務が曖昧になる
- 「下書き保存」と「申請提出」の違いが弱くなる

### 案B: request が approval_policy を直接参照する

- approval_policy の変更時に、過去 request の意味が変わってしまう
- 監査や説明責任の観点で弱い

### 案C: request が `applied_approval_route_id` を直接持つ

- 今回は `applied_approval_route` を request に従属する適用結果として扱っている
- 外部キーの向きを逆にすると、従属関係の説明がやや不自然になる

## 受け入れる制約

- draft 時点では承認ルートはまだ存在しない
- submit 時に approval_policy を評価できない場合は申請を成立させない
- 承認ルート適用の責務が submit 処理に集まる

## 見直す条件

- draft 時点で承認見込みをUI表示したくなった場合
- 申請前レビューや事前見積りのために、draft 中も承認ルートの仮表示が必要になった場合
- 即時提出と下書き保存を同一 API で扱いたくなった場合
