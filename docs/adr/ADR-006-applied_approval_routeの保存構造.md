# ADR-006: applied_approval_route の保存構造

- Status: **Superseded by [ADR-009](./ADR-009-applied_approval_routeを親子構造で保持する.md)**
- Date: 2026-03-24

> **この ADR は読む必要がない**: 結論は ADR-009 と同一で、ADR-009 のほうが検討した案と ADR-010 との整合まで書かれている。
>
> - 承認ルートの適用結果を**なぜ保存するのか** → [ADR-008](./ADR-008-承認ルートの適用結果をrequestに紐づけて保持する.md)
> - **どの粒度で保存するのか** → [ADR-009](./ADR-009-applied_approval_routeを親子構造で保持する.md)
>
> 以下は判断の経緯として残すもので、現在の設計の根拠は ADR-009 にある。

---

当時の決定は次のとおりだった。

applied_approval_route を `applied_approval_routes`（ルート1件）→ `applied_approval_route_steps`（段ごとの記録）→ `applied_approval_route_step_approvers`（段ごとの承認者）の3層構造で保存する。

理由として挙げていたのは、1段に複数の承認者が付きうる業務ルールを正規化して持つには段を中間概念として持つ必要があること、申請時点の確定記録として保持することで approval_policy や承認者マスタの変更が過去 request に影響しないことの2点である。

検討した他の案として、ルートと承認者を直接結ぶ2層構造（1段に複数承認者がいる場合を表現できない）と、JSON で1カラムに保存する案（承認者ごとの完了状態を SQL で扱いにくい）を挙げていた。

この内容はすべて ADR-009 に引き継がれている。
