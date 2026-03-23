## 概要

本書は、経費申請アプリで扱う主要な業務概念と、それぞれの役割を整理するための設計概要である。
目的は、ER図、API設計、認可設計へ進む前に、**各概念が何を表すのか、どの情報をどこで持つのか**を明確にすることである。

---

## 設計方針

本アプリでは、状態、履歴、設定、権限、監査を1つの概念にまとめない。
それぞれの役割を分けて持つ。特に、次の分離を重視する。

- `request` は、**1つの申請系列のうち、ある1版の申請データ**を表し、その版の現在状態を持つ
- `approval` は、**その申請に対して誰がいつどんな判断をしたか**を記録する履歴である
- `approval_policy` は、**どの条件の申請にどの承認ルートを適用するか**を決めるルールである
- `applied_approval_route` は、**request作成時に確定した承認ルートの保存結果**である
- `role` は、**その組織で何の操作ができるか**を表す
- `audit_log` は、**重要操作を後から確認できるように残す記録**であり、申請や承認そのものではない

---

## 主要概念

### `request`

`request` は、1つの申請版を表す。
申請内容、その版の現在状態、現在何段目の承認待ちかを保持する。

また、申請時点で確定した承認ルートを参照する。

### `approval`

`approval` は、`request` に対して行われた判断の記録である。
誰が、いつ、承認・差し戻し・却下のどれを行ったかを表す。

`approval` 自体は、「現在どの段階にいるか」「次に誰が承認するか」といった進行中の情報は持たない。
それらは `request` 側で管理する。

### `approval_policy`

`approval_policy` は、承認ルートを決めるルールである。
たとえば、所属組織、申請種別、金額条件などに応じて、承認段数や承認対象者の条件を決める。

### `applied_approval_route`

`applied_approval_route` は、`request` 作成時に `approval_policy` を評価して確定した承認ルートの保存結果である。
後から `approval_policy` が変わっても、既存の `request` には申請時点で確定した承認ルートを使い続ける。

### `organization`

`organization` は、利用者が所属する組織の単位である。
また、「どの組織のデータか」「どの組織でその権限が有効か」を決める基準でもある。

### `role`

`role` は操作権限の区分である。
たとえば、「申請できる」「承認できる」「管理設定を変更できる」といった違いを表す。

`role` は承認段数や金額条件を表さない。
それらは `approval_policy` 側で扱う。

### `audit_log`

`audit_log` は、重要操作を後から確認するための記録である。
申請・承認・ユーザー変更・ロール変更・ポリシー変更・ログインなどを記録対象とする。

`audit_log` は `request` や `approval` の代わりではない。
申請データ本体とは別に持つ。

### `organization_membership`

`organization_membership` は、`user` と `organization` の対応関係を表す中間概念である。
1人の `user` が複数の `organization` に所属でき、1つの `organization` に複数の `user` が所属できるため、この概念を分けて持つ。

また、`role` は `user` に直接付与せず、この `organization_membership` に対して付与する。

---

## 概念同士の関係

### `user` と `organization`

`user` と `organization` は多対多の関係である。
そのため、両者の対応関係は `organization_membership` で管理する。

### `user` と `role`

`user` と `role` は直接結ばない。
同じ `user` でも組織ごとに権限が変わるため、`role` は `organization_membership` に対して付与する。

### `organization_membership` と `role`

1つの `organization_membership` には、1つ以上の `role` が付与されうる。
これにより、「同じ利用者が、組織Aでは管理者、組織Bでは申請者」といった状態を表現できる。

### `request` と `approval`

1つの `request` に対して、`approval` は0件以上発生する。
`approval` は判断履歴であり、`request` の現在状態そのものではない。

### `request` と `approval_policy` / `applied_approval_route`

`request` 作成時には、その時点の `approval_policy` を評価して、適用する承認ルートを決定する。
その結果を `applied_approval_route` として保存し、以後その `request` はその保存結果を参照する。

### `audit_log` と各概念

`audit_log` は、`request`、`approval`、`approval_policy`、`user` などの対象識別子を保持する。
これにより、「誰が、どの対象に対して、いつ、何をしたか」を後から追跡できる。

---

## 認可の考え方

認可判定では、少なくとも次の条件を確認する。

- 利用者が認証済みであること
- 対象の `request` が属する `organization` において、必要な `role` を持っていること
- その `request` の現在承認段の対象者であること
- その `request` に適用された承認ルート上で、その利用者が承認対象者に含まれていること

認可は画面表示だけでなく、API側でも必ず強制する。

---

## 未確定論点

- `applied_approval_route` をどの単位で保存するか
- 閲覧権限の細かい条件をどこまで `role` で表すか
- 代理承認、代理申請を Phase 1 に含めるか
- 同一承認段に複数承認者がいる場合に、主担当者という概念を持つか
