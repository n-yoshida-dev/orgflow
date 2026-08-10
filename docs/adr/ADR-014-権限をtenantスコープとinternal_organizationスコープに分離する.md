# ADR-014: 権限を tenant スコープと internal organization スコープに分離する

- Status: Accepted
- Date: 2026-04-06
- 関連: ADR-013（tenant と internal organization の分離） / ADR-003（role の方針） / ADR-027（管理系操作の割り当て）

> **決定**: 権限を `TENANT_ROLE` と `INTERNAL_ORGANIZATION_ROLE` の2種類に分離し、それぞれの付与関係を `TENANT_MEMBERSHIP_ROLES` と `INTERNAL_ORGANIZATION_MEMBERSHIP_ROLES` で表す。

## 背景

tenant と internal organization を別概念として分離した結果（ADR-013）、権限にも少なくとも2つのスコープがあることがはっきりした。

- tenant 全体に対する管理系操作（利用者の追加、tenant 設定）
- internal organization 単位の業務操作（申請、承認、承認ルート設定）

これらを1種類の role で表すと、workspace の切替単位である tenant と、その中の部門・チームの業務権限が同じ階層に並んでしまう。

## 理由

- tenant 全体管理者を、特定部門への所属と切り離して表現できる
- 同一 tenant 内で複数の internal organization を横断する承認一覧や監査ログ閲覧を、tenant スコープの権限として整理できる
- 部門単位の業務権限（申請、承認、承認ルート設定）を internal organization スコープに閉じられる
- tenant 切替時に、どの権限が引き継がれてどの権限が組織依存かを説明できる

## 検討した他の案

- **案A: 1種類の role で tenant と internal organization の両方を表す** — tenant 全体管理者と部門権限が同じ集合に混ざり、role の意味が広がりすぎる。ER や API での表現も曖昧になる
- **案B: tenant 全体管理者も internal organization の role で代用する** — tenant 全体の管理権限が特定部門への所属に引きずられる。部門を持たない管理者を表現できない

## 受け入れる制約

- 概念数と中間テーブルが増える
- Phase 1 では tenant_role と internal_organization_role の境界を最小限に絞る必要がある
- 画面や seed データの都合上、tenant 管理者にも internal organization 所属を与える場合がある

## 見直す条件

- tenant 単位の管理機能を Phase 1 のスコープから外す場合
- 2つの role を統合しても不整合が起きないと判断できた場合
- permission ベースのより汎用的な認可モデルへ拡張する場合
