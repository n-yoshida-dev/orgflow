ADR-015: shared DB 前提で tenant_id により分離する

- Status: Accepted
- Date: 2026-04-07

## 背景

tenant と internal organization を分離し、current tenant を切り替えながら、
同一 tenant 内では複数 internal organization を横断して request や audit_log を閲覧できる前提を採った。

このとき、マルチテナントのデータ配置をどうするかを決める必要があった。

候補としては、少なくとも次があった。

- tenant ごとに DB を分ける
- 1 つの DB の中で tenant_id により分離する

決めないと、internal_organization_memberships の tenant 整合性、approval_route_step_approvers の参照方針、current tenant の扱い、物理ER図の制約方針が曖昧になる。

## 採用した案

OrgFlow は Phase 1 では **shared DB 前提** で進める。  
tenant 境界は tenant_id により表現し、別 tenant のデータは current tenant を切り替えない限り参照できない前提とする。

## 採用理由

- 1 つの DB の中で tenant / internal organization / request / approval / audit_log の関係を一貫して表現しやすい
- current tenant を前提にした承認一覧、監査ログ閲覧、設定管理の説明がしやすい
- tenant ごとに DB を分ける設計に比べて、Phase 1 の実装・検証・README / ADR 説明のコストを抑えやすい
- physical ER で tenant 一致制約や検索用 index を考えやすい
- まずは B2B SaaS のバックエンド基盤を説明可能にすることを優先する、という Phase 1 の目的と合う

## 不採用案

### 案A: tenant ごとに DB を分離する

- tenant 境界の説明は明快になる
- ただし、Phase 1 では構成・運用・接続先切替の複雑さが増えすぎる
- current tenant 切替を DB 接続切替として扱う必要があり、今の学習目的に対して重い

## 受け入れる制約

- tenant 境界を DB 制約とアプリケーション側チェックの両方で意識する必要がある
- approver や membership の tenant 整合性は、単純な単独FKだけでは表現しきれない
- current tenant を前提にした設計・テストが必要になる

## 見直す条件

- tenant ごとに完全分離が必要な非機能要件が生じた場合
- Phase 2 以降で、tenant ごとの DB 分離が必要なほど運用要件が重くなった場合
- shared DB 前提では監査・権限・性能の説明が苦しくなった場合
