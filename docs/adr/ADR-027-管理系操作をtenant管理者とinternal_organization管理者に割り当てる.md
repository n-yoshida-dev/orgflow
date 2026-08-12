# ADR-027: 管理系操作を tenant 管理者と internal organization 管理者に割り当てる

- Status: Accepted
- Date: 2026-04-10
- 関連: ADR-014（権限スコープの分離） / ADR-025（role の付与構造）

> **決定**: 利用者と所属の管理は tenant 管理者、承認ルールと組織内 role の管理は internal organization 管理者に割り当てる。

| 実行者 | 操作 |
|---|---|
| tenant 管理者 | tenant_membership_role の付与 / 剥奪 / 一覧取得、user の作成 / 更新 / 削除 / 一覧取得、internal_organization_membership への user 追加 / 削除 |
| internal organization 管理者 | approval_policy の作成 / 更新 / 削除 / 一覧取得、approval_route の作成 / 更新 / 削除 / 一覧取得、internal_organization_membership_role の付与 / 剥奪 / 一覧取得 |

## 背景

ADR-014 で権限を2つのスコープに分離したが、個々の管理操作をどちらのスコープに置くかは決めていなかった。

特に `internal_organization_membership_role` の付与は、どちらにも寄せられる。tenant 管理者に寄せれば管理者が1種類で済むが、部門の業務権限まで tenant 管理者が握ることになる。

## 理由

- 実際の運用では、部門の管理者が自部門のメンバーへ承認権限を付与する。tenant 管理者に集中させると、部門の人事異動のたびに tenant 管理者への依頼が発生する
- `approval_policy`、`approval_route`、`internal_organization_membership_role` はいずれも組織単位の業務運用に属する。同じスコープに揃えると、承認まわりの管理が1人の管理者で完結する
- 利用者そのものの作成・削除と、どの組織に所属させるかは tenant 全体の話であり、部門管理者の権限にすると他部門の利用者まで操作できてしまう
- 一覧取得は作成・更新・削除と事前条件が異なる。状態変更は対象の membership が存在することを前提にするが、一覧取得は組織配下の付与結果の集合を返す操作なので、対象を internal organization とみなす

## 検討した他の案

- **案A: `internal_organization_membership_role` の管理も tenant 管理者に寄せる** — tenant 全体管理と部門単位の業務権限管理が混ざり、ADR-014 で分離した意味が薄れる
- **案B: 一覧取得の事前条件を付与・剥奪と揃え、対象 membership の存在を要求する** — 一覧取得の対象は組織配下の付与結果の集合であり、単一 membership の存在確認と粒度が合わない

## 受け入れる制約

- internal organization 管理者の責務が tenant 管理者より細かく見えるため、README と OpenAPI での補足が必要になる
- membership 単位の内部表現と、組織配下の一覧を返す API 表現を分けて考える必要がある

## 見直す条件

- internal organization 管理者が複数部門を横断して role を管理する必要が出た場合
- role の付与対象を user 単位・所属単位・兼務単位で再整理する必要が生じた場合
- permission ベースのより細かい認可モデルへ移行する場合
