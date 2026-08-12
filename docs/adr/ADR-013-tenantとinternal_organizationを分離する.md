# ADR-013: tenant と internal organization を分離する

- Status: Accepted
- Date: 2026-04-06

> **決定**: tenant（契約主体かつデータ分離の最上位単位）と internal organization（tenant 内部の部門・課・チーム）を別概念として分離する。

## 背景

これまで organization という 1 つの概念で、次の役割をまとめて扱っていた。

- データ分離の境界
- ユーザー所属先
- request の帰属先
- role 付与の単位
- 組織切替の対象

しかし、次のユースケースがある。

- 同一ユーザーが 株式会社ABC と サッカークラブ の両方に所属できる
- 株式会社ABC の中で、人事部と経理部の両方に承認権限を持てる
- 承認一覧と監査ログ閲覧は、同一 tenant 内では複数部門を横断できる
- ただし別 tenant のデータは、明示的に切り替えない限り見えない

このため、workspace 切替の対象と、同一 workspace 内の部門・チームの概念を分ける必要が生じた。

## 採用した案

tenant と internal organization を分離する。

- tenant: 契約主体かつデータ分離の最上位単位
- internal organization: tenant 内部の部門・課・チーム

## 採用理由

- 別 tenant のデータ分離境界を明示できる
- 同一 tenant 内で複数部門を横断できる要件を表現しやすい
- 承認一覧、監査ログ閲覧、申請作成、承認ルート設定の責務を整理しやすい
- workspace 切替と部門権限を別概念として説明できる

## 不採用案

### 案A: organization 1段で持ち続ける

- tenant 境界と部門境界が混ざる
- 承認一覧や監査ログ閲覧の横断範囲を説明しにくい
- request の帰属先と role 付与単位の意味が曖昧になる

## 受け入れる制約

- 概念数とテーブル数が増える
- Phase 1 の既存ER図、docs、OpenAPI骨子の見直しが必要になる

## 見直す条件

- Phase 1 では tenant と internal organization を分ける必要が薄いと判断した場合
- 部門階層を持たず、tenant 1段で十分なユースケースへ縮小する場合
