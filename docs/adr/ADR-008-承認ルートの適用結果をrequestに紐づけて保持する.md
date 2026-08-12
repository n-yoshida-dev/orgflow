# ADR-008: 承認ルートの適用結果を request に紐づけて保持する

- Status: Accepted
- Date: 2026-03-24
- 関連: ADR-009（適用結果の保存粒度） / ADR-012（policy と route の参照方向） / ADR-004（状態遷移）

> **決定**: request は approval_policy を直接参照しない。submit 時に承認ルートを評価し、その結果を applied_approval_route として保存する。`applied_approval_routes.request_id` に一意制約を置き、1 request に 1 applied_approval_route とする。

## 背景

draft の作成時点では申請内容がまだ確定していないため、承認ルートを固定できない。一方 submit 時には、approval_policy を評価してどの承認ルートを使うかを確定する必要がある。

さらに、申請後に approval_policy や approval_route が変更されても、提出済み request の承認ルートが変わってはいけない。ルール定義そのものを参照し続けると、過去の request の意味が後から変わってしまう。

## 理由

- draft の編集中に承認ルートを固定しないため、内容が変わるたびに再評価する必要がない
- 「この request には submit 時点でこのルートが適用された」という事実を保存できる
- approval_policy や approval_route を後から変更しても、提出済み request に影響しない
- 承認処理を request 単位で完結させられる
- 1対1の関係を `applied_approval_routes.request_id` の一意制約で表せる。適用結果が request に従属するという意味と外部キーの向きが一致する

## 検討した他の案

- **案A: draft 作成時に承認ルートを確定する** — draft は未確定要素が多く、編集のたびに再評価が必要になる。create と submit の責務も曖昧になる
- **案B: request が approval_policy を直接参照する** — approval_policy を変更すると過去の request の承認ルートまで変わってしまう
- **案C: request が `applied_approval_route_id` を持つ** — 外部キーの向きが逆になり、適用結果が request に従属するという関係と一致しなくなる

## 受け入れる制約

- draft の時点では承認ルートが存在しないため、承認見込みを表示できない
- submit 時に approval_policy を評価できない場合は申請を成立させられない
- 承認ルート適用の責務が submit 処理に集中する

## 見直す条件

- draft の時点で承認見込みを UI に表示したくなった場合
- 申請前レビューや事前見積りのために draft 中も承認ルートの仮表示が必要になった場合
- 即時提出と下書き保存を同一 API で扱いたくなった場合
