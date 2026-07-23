# ADR-014: tenant スコープ権限と internal organization スコープ権限を分離する

- Status: Accepted
- Date: 2026-04-06
- 最終追記: 2026-04-10

> **決定**: 権限を `TENANT_ROLE` と `INTERNAL_ORGANIZATION_ROLE` に分離し、付与関係を各 membership_roles で表す。internal_organization_membership_role の付与・剥奪・一覧取得は internal organization 管理者の責務とする。

## 背景

tenant と internal organization を分離した結果、権限にも少なくとも 2 つのスコープがあることが明確になった。

- tenant 全体に対する管理系操作
- internal organization 単位の業務操作

これらを 1 種類の role で表すと、workspace 切替の対象である tenant と、同一 tenant 内の部門・チームの業務権限が混ざる。

## 採用した案

権限を次の 2 つに分離する。

- `TENANT_ROLE`
  tenant 全体に対する管理系操作権限
- `INTERNAL_ORGANIZATION_ROLE`
  internal organization への所属関係に対して付与される業務操作権限

また、それぞれの付与関係を次の中間概念で表す。

- `TENANT_MEMBERSHIP_ROLES`
- `INTERNAL_ORGANIZATION_MEMBERSHIP_ROLES`

## 採用理由

- tenant 全体管理者を、部門単位の internal_organization_role と混同せずに表現できる
- tenant 切替と tenant 単位管理の説明がしやすくなる
- 同一 tenant 内で複数 internal organization を横断する承認一覧や監査ログ閲覧を整理しやすい
- internal organization 単位の request / approval / approval policy の業務権限と、tenant 単位の管理権限を分離できる

## 不採用案

### 案A: `ROLE` だけで tenant と internal organization の両方を表す

- tenant 全体管理者と部門権限が混ざる
- role の意味が広がりすぎる
- docs / ER / API で説明しにくい

### 案B: tenant 全体管理者も internal organization の internal_organization_role で代用する

- tenant 全体管理権限が、特定部門への所属に引きずられる
- tenant 単位の請求、監査、設定管理の責務を表しにくい

## 受け入れる制約

- 概念数と中間テーブル候補が増える
- Phase 1 では tenant_role と internal_organization_role の境界を最小限に絞る必要がある
- 画面や seed データの簡略化のため、実装上は tenant 管理者にも internal organization 所属を与える可能性がある

## 見直す条件

- tenant 単位管理機能を Phase 1 で扱わない方針に縮小する場合
- tenant_role と internal_organization_role を統合しても説明上の不整合が起きないと判断した場合
- より汎用的な permission モデルへ拡張する場合

## 追記: 業務操作一覧における管理系操作の責務境界（2026-04-10）

### 決定

tenant スコープ権限と internal organization スコープ権限の使い分けを、業務操作一覧では次のように具体化する。

- tenant 管理者が実行する操作
  - tenant_membership_role の付与 / 剥奪 / 一覧取得
  - user の作成 / 更新 / 削除 / 一覧取得
  - internal_organization_membership への user 追加 / 削除

- internal_organization 管理者が実行する操作
  - approval_policy の作成 / 更新 / 削除 / 一覧取得
  - approval_route の作成 / 更新 / 削除 / 一覧取得
  - internal_organization_membership_role の付与 / 剥奪 / 一覧取得

特に、`internal_organization_membership_role` の付与・剥奪・一覧取得は tenant 管理者ではなく、**対象 internal_organization の管理権限を持つ internal_organization 管理者** が実行する。

また、`internal_organization_membership_role` 一覧取得の対象は internal_organization とみなし、状態変更系の単一 membership 存在確認とは分けて扱う。

### 採用理由

- tenant 全体の利用者・所属管理と、部門単位の業務権限管理を分けた方が責務境界を説明しやすい
- 実務上も、「課長や部長が、自部門内のメンバーへ承認権限を付与する」といった運用の方が自然であり、tenant 管理者だけに集中させるより現実の業務に近い
- approval_policy / approval_route / internal_organization_membership_role は、いずれも internal organization 単位の業務運用に近く、同じスコープで揃えた方が理解しやすい
- 一覧取得は作成・更新・削除とは違い、存在確認と閲覧権限の成立が中心であり、状態変更系と同じ事前条件に寄せると不自然になる

### 不採用案

- 案A: internal_organization_membership_role の付与 / 剥奪 / 一覧取得も tenant 管理者に寄せる
  - tenant 全体管理と部門単位の業務権限管理が混ざる
  - approval_policy / approval_route / role 付与結果の責務境界がぼやける

- 案B: internal_organization_membership_role 一覧取得の事前条件を、付与 / 剥奪と同じ「対象 membership が存在すること」に寄せる
  - 一覧取得の対象は internal_organization 配下の role 付与結果の集合であり、状態変更系の単一対象存在確認と同じ粒度で扱うと不自然になる

### 受け入れる制約

- internal_organization 管理者の責務が tenant 管理者より細かく見えるため、README / OpenAPI で補足が必要になる
- membership 単位の内部表現と、internal_organization 配下の一覧取得という API 表現を分けて考える必要がある

### 見直す条件

- internal_organization 管理者が、自部門配下だけでなく複数部門を横断して internal_organization_membership_role を管理する必要が出た場合
- role 付与対象を user 単位・membership 単位・兼務単位などで再整理する必要が生じた場合
- 将来、permission ベースのより細かい認可モデルへ移行する場合
