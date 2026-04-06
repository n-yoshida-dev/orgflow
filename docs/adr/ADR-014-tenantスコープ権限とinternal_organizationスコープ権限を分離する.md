# ADR-014: tenant スコープ権限と internal organization スコープ権限を分離する

- Status: Accepted
- Date: 2026-04-06

## 背景

tenant と internal organization を分離した結果、権限にも少なくとも 2 つのスコープがあることが明確になった。

- tenant 全体に対する管理系操作
- internal organization 単位の業務操作

これらを 1 種類の role で表すと、workspace 切替の対象である tenant と、同一 tenant 内の部門・チームの業務権限が混ざる。

## 採用した案

権限を次の 2 つに分離する。

- `TENANT_ROLE`
  tenant 全体に対する管理系操作権限
- `ROLE`
  internal organization への所属関係に対して付与される業務操作権限

また、それぞれの付与関係を次の中間概念で表す。

- `TENANT_MEMBERSHIP_ROLES`
- `MEMBERSHIP_ROLES`

## 採用理由

- tenant 全体管理者を、部門単位の role と混同せずに表現できる
- tenant 切替と tenant 単位管理の説明がしやすくなる
- 同一 tenant 内で複数 internal organization を横断する承認一覧や監査ログ閲覧を整理しやすい
- internal organization 単位の request / approval / approval policy の業務権限と、tenant 単位の管理権限を分離できる

## 不採用案

### 案A: `ROLE` だけで tenant と internal organization の両方を表す

- tenant 全体管理者と部門権限が混ざる
- role の意味が広がりすぎる
- docs / ER / API で説明しにくい

### 案B: tenant 全体管理者も internal organization の role で代用する

- tenant 全体管理権限が、特定部門への所属に引きずられる
- tenant 単位の請求、監査、設定管理の責務を表しにくい

## 受け入れる制約

- 概念数と中間テーブル候補が増える
- Phase 1 では tenant role と internal organization role の境界を最小限に絞る必要がある
- 画面や seed データの簡略化のため、実装上は tenant 管理者にも internal organization 所属を与える可能性がある

## 見直す条件

- tenant 単位管理機能を Phase 1 で扱わない方針に縮小する場合
- tenant role と internal organization role を統合しても説明上の不整合が起きないと判断した場合
- より汎用的な permission モデルへ拡張する場合
