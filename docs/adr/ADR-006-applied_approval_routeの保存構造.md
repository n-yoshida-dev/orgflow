# ADR-006: applied_approval_route の保存構造

- Status: Superseded by [ADR-009](./ADR-009-applied_approval_routeを親子構造で保持する.md)
- Date: 2026-03-24

> **決定**: applied_approval_route を `applied_approval_routes` → `applied_approval_route_steps` → `applied_approval_route_step_approvers` の3層構造で保存する。
>
> **この ADR は参照不要**: 結論は [ADR-009](./ADR-009-applied_approval_routeを親子構造で保持する.md) と同一で、ADR-009 の方が不採用案（1段1承認者に制限する案）と ADR-010 との整合まで書かれている。
> 「なぜ適用結果を保存するのか」は [ADR-008](./ADR-008-承認ルートの適用結果をrequestに紐づけて保持する.md) が扱う。
> 以降は ADR-008（保存する理由）と ADR-009（保存する粒度）を参照すること。本文は判断の経緯として残す。

## 背景

- submit 時に approval_policy を評価して確定した承認ルートを保存する必要がある
- 保存しないと、後から approval_policy が変更された場合に過去 request の承認ルートも変わり、監査・追跡が困難になる
- 1段に複数の承認者が付きうるため、ルートと承認者を直接結ぶだけでは表現できない構造になる

## 採用した案

- applied_approval_routes（ルート1件）→ applied_approval_route_steps（段ごとの記録）→ applied_approval_route_step_approvers（段ごとの承認者）の3層構造で保存する

## 採用理由

- 1段に複数承認者が付きうる業務ルールを正規化して持つには、段を中間概念として持つ必要がある
- 申請時点の確定記録として保持するため、approval_policy や承認者マスタの変更が過去 request に影響しないことを保証できる
- 同一段に複数承認者がいる場合は誰か1人の承認で次段へ進むルールを採用しており、step 単位で承認者リストを持つ構造と整合する

## 不採用案

- ルートと承認者を直接結ぶ2層構造: 1段に複数承認者がいる場合に表現できない
- JSON で1カラムに保存する案: 承認者ごとの完了状態をSQLで扱いにくく、テスト・監査の観点から追跡しづらい

## 受け入れる制約

- テーブルが3つになるため JOIN が増える
- 全員承認が必要なユースケースは、承認段を分けて順番に承認させることで対応する（Phase 1 では全員承認の直接表現はしない）

## 見直す条件

- 全員承認ルールを直接データで表現する必要が生じた場合
- 承認ルートの複雑度が上がり、3層構造ではクエリが現実的でなくなった場合
