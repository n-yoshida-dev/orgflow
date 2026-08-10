# ADR-015: shared DB 前提で tenant 境界を tenant_id により表現する

- Status: Accepted
- Date: 2026-04-07
- 関連: ADR-013（tenant と internal organization の分離） / ADR-026（申請先の条件） / ADR-024（current_tenant_id）

> **決定**: Phase 1 は shared DB 前提とし、tenant 境界を `tenant_id` で表現する。DB 制約で表現できる tenant 一致は physical ER に落とし、業務操作の許可条件は認可ロジック側に残す。

## 背景

tenant と internal organization を分離し、current tenant を切り替えながら、同一 tenant 内では複数の internal organization を横断して request や audit_log を閲覧する前提を採った（ADR-013）。

このとき、マルチテナントのデータをどこに置くかを決める必要があった。候補は tenant ごとに DB を分ける案と、1つの DB の中で `tenant_id` により分離する案の2つ。

決めないと、所属の tenant 整合性、承認者の参照方針、current tenant の扱い、physical ER の制約方針がすべて曖昧なまま進むことになる。

## 理由

- 1つの DB の中で tenant / internal organization / request / approval / audit_log の関係を一貫して表現できる
- current tenant を前提にした承認一覧、監査ログ閲覧、設定管理を、接続先の切替なしに実装できる
- tenant 一致制約や検索用インデックスを physical ER 上で設計できる
- tenant ごとに DB を分ける構成に比べ、Phase 1 で必要な構成・運用・接続切替が少ない

## tenant 一致をどこまで DB 制約で表現するか

shared DB では、tenant 境界をアプリケーション側の注意だけに委ねると、境界が守られている根拠を示せない。DB で表現できるものは DB 制約に落とす。

- `request` は `tenant_id` を持つ
- `request` の申請者が current tenant に属する user であることを、tenant への所属を通して表現する
- `request` の申請先 internal organization が同じ `tenant_id` に属することを表現する
- `applied_approval_route_step` は `request_id` を持ち、request / approval / current step の整合を複合参照で表現する

一方、申請先に選べる組織の判定（ADR-026）のような業務上の許可条件は、current tenant と role を使う認可ロジック側に残す。DB 制約で表せるのは「同じ tenant に属していること」までで、「その組織へ申請してよいか」は表せない。

## 検討した他の案

- **案A: tenant ごとに DB を分離する** — tenant 境界の説明は最も明快だが、current tenant の切替を DB 接続の切替として扱う必要があり、構成と運用が Phase 1 のスコープに対して重い

## 受け入れる制約

- tenant 境界を DB 制約とアプリケーション側チェックの両方で意識する必要がある
- 承認者や所属の tenant 整合性は、単独の外部キーだけでは表現しきれず複合参照が必要になる
- current tenant を前提にした設計とテストが必要になる
- すべてのクエリで `tenant_id` の絞り込み漏れが事故に直結する

## 見直す条件

- tenant ごとの完全分離が必要な非機能要件が生じた場合
- Phase 2 以降で、tenant ごとの DB 分離が必要なほど運用要件が重くなった場合
- shared DB 前提では監査・権限・性能の要求を満たせなくなった場合
