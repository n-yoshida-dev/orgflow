## 概要

本書は、OrgFlow で扱う主要な業務概念と、それぞれの役割を整理するための設計概要である。  
目的は、ER図、API設計、認可設計へ進む前に、**各概念が何を表すのか、どの情報をどこで持つのか** を明確にすることである。

OrgFlow は、単なる CRUD アプリではなく、申請・承認・監査・権限制御・データ分離を含む B2B ワークフロー SaaS を題材にする。  
そのため、状態、履歴、設定、権限、監査、テナント境界を 1 つの概念にまとめず、役割ごとに分けて扱う。

---

## 設計方針

本アプリでは、次の分離を重視する。

- `request` は、**ある 1 件の申請版**を表し、その版の申請内容と現在状態を持つ
- `approval` は、**その request に対して誰がいつどんな判断をしたか**を記録する履歴である
- `approval_policy` は、**どの条件の申請にどの承認ルートを適用するか**を決めるルールである
- `applied_approval_route` は、**request 作成時に確定した承認ルートの保存結果**である
- `role` は、**ある所属関係に対して何の操作ができるか**を表す
- `audit_log` は、**重要操作を後から確認できるように残す記録**であり、申請や承認そのものではない
- `tenant` は、**契約主体かつデータ分離の最上位単位**である
- `internal_organization` は、**tenant 内部の部門・課・チーム**を表す

この文書では、特に `tenant` と `internal_organization` を分けて扱う。  
別 tenant のデータは、明示的に tenant を切り替えない限り参照できない。  
一方で、同一 tenant 内では、権限があれば複数の internal organization を横断して request や audit_log を参照できる。

---

## テナント境界と内部組織境界

### tenant

`tenant` は、株式会社ABC やサッカークラブのような、データ分離の最上位単位を表す。  
tenant をまたぐと、設定、request、approval、audit_log は分離される。  
画面上でも、別 tenant のデータを見るには明示的な tenant 切り替えが必要である。

### internal organization

`internal_organization` は、tenant の内部にある部門・課・チームを表す。  
例として、株式会社ABC の中の人事部、経理部、営業1課などがこれに当たる。

internal organization は、申請の帰属先、承認ルートの適用先、所属とロールの評価単位として使う。  
ただし、同一 tenant 内では、権限があれば複数の internal organization を横断した一覧表示や検索を認める。

---

## 主要概念

### `tenant`

`tenant` は、契約主体かつデータ分離の最上位単位である。  
ユーザーは複数 tenant に所属できる。  
ただし、ある時点で操作対象になるのは 1 つの current tenant である。

current tenant は、一覧取得、監査ログ閲覧、設定変更などの API がどのデータ範囲に対して実行されるかを決める前提になる。

### `internal_organization`

`internal_organization` は、tenant 内部の部門・課・チームを表す。  
request は、この internal organization に帰属する申請として扱う。  
approval_policy や承認ルートも、基本的に internal organization 単位で管理する。

### `user`

`user` は、システムを利用する人を表す。  
ユーザーそのものに直接 role を付与するのではなく、どの tenant / internal organization にどう所属しているかを通して権限を評価する。

### `internal_organization_membership`

`internal_organization_membership` は、user が internal organization に所属しているという関係を表す。  
1 人の user は複数の internal organization に所属できる。  
また、同じ user が複数 tenant に所属することもありうる。

この概念は「現在の所属関係」を表すものであり、過去 request の帰属を不変に保持するための参照先そのものではない。

### `membership_role`

`membership_role` internal_organization_membership に対して付与される role を表す。  
同じ user でも、所属先の internal organization ごとに異なる role を持てる。

ここでいう role は、あくまで「何の操作ができるか」を表す。  
承認金額条件や承認段数は role ではなく approval_policy 側で扱う。

### `request`

`request` は、1 件の申請版を表す。  
申請内容、その版の現在状態、現在どの承認段にいるかを持つ。

また、request は**申請時点の不変な事実**として、申請者 user と申請先 internal organization を直接持つ。  
現在の所属関係を表す internal_organization_membership を参照して過去 request の帰属を表すのではない。

差し戻し後の再申請では既存 request を更新せず、新しい request を作成する。  
そのため、request は「申請全体」ではなく、「ある系列の中の 1 版」を表す。

### `approval`

`approval` は、request に対する 1 回の判断履歴を表す。  
承認、差し戻し、却下などを記録する。

approval は現在状態そのものではない。  
現在その request がどの状態にあるか、どの承認段を処理中かは request 側で管理する。

### `approval_policy`

`approval_policy` は、どの条件の request にどの承認ルートを適用するかを決めるルールである。  
request 種別、金額条件、internal organization などを基準に評価される。

approval_policy は、操作権限ではない。  
「誰が何をできるか」は role が担い、  
「どの申請にどの承認ルートを適用するか」は approval_policy が担う。

### `approval_route`

`approval_route` は、承認段と承認者の並びを表すテンプレートである。  
複数の approval_policy が同じ approval_route を参照できる。

### `applied_approval_route`

`applied_approval_route` は、request 作成時に approval_policy を評価して確定した承認ルートの保存結果である。  
request 作成後に approval_policy や approval_route が変更されても、既存 request の承認ルートは変わらない。

この概念により、承認処理・監査・後追い説明を request 単位で安定して行える。

### `audit_log`

`audit_log` は、誰が、どの tenant / internal organization 文脈で、いつ、何をしたかを残す独立した記録である。  
approval の代用品ではない。

ログイン、申請作成、承認、差し戻し、設定変更、ロール変更など、重要操作を横断的に記録する。  
同一 tenant 内では、権限があれば複数 internal organization を横断して検索できる。  
ただし、別 tenant の audit_log は current tenant を切り替えない限り参照できない。

---

## 概念どうしの関係

- 1 つの tenant は複数の internal organization を持つ
- 1 人の user は複数の internal organization に所属できる
- internal_organization_membership は user と internal organization の所属関係を表す
- membership_role は internal_organization_membership に対して role を付与する
- 1 件の request は 1 つの internal organization に属する
- 1 件の request は複数の approval を持ちうる
- 1 件の request は 1 つの applied_approval_route を持つ
- approval_policy は approval_route を参照し、request 作成時に applied_approval_route へ評価結果が保存される
- audit_log は request / approval / approval_policy / user などを横断的に記録する

---

## 認可の考え方

認可は、user 単位ではなく、**current tenant 内の internal_organization_membership と membership_role** を通して評価する。  
同じ user でも、所属する internal organization が変われば、できる操作は変わりうる。

ただし、一覧系や検索系では、current tenant 内で権限のある複数 internal organization を横断表示することがある。  
例として、承認一覧や監査ログ閲覧は、明示的に internal organization を切り替えなくても、権限がある範囲を横断して表示してよい。

一方で、別 tenant のデータは current tenant を切り替えない限り参照できない。

---

## 権限スコープの分離

OrgFlow では、権限を次の 2 つのスコープに分けて扱う。

- `TENANT_ROLE`
  tenant 全体に対して実行できる管理系操作権限を表す。
  例: tenant 設定変更、tenant 内ユーザー管理、tenant 単位の請求管理、tenant 全体の監査ログ閲覧

- `ROLE`
  internal organization への所属関係に対して付与される業務操作権限を表す。
  例: request 作成、request 承認、request 閲覧、approval policy 管理

この分離により、workspace 切替の対象である tenant と、同一 tenant 内の部門・チームでの業務操作権限を混同しないようにする。

---

## membership の意味

- `TENANT_MEMBERSHIP`
  user が tenant に所属している事実を表す。

- `INTERNAL_ORGANIZATION_MEMBERSHIP`
  tenant の中で、user が internal organization に所属している事実を表す。

`INTERNAL_ORGANIZATION_MEMBERSHIP` は tenant の中での配属を表す概念であり、tenant への所属そのものを表す概念ではない。

---

## tenant 境界とデータ分離

OrgFlow は Phase 1 では shared DB 前提で構成する。  
別 tenant のデータは current tenant を切り替えない限り参照できない。

この前提で、データ分離の最上位単位を `tenant` とし、
tenant の内部にある部門・課・チームを `internal organization` とする。

- `tenant`
  契約主体かつデータ分離の最上位単位
- `internal organization`
  tenant 内部の部門・課・チーム

承認一覧や監査ログ閲覧は、current tenant 内で複数 internal organization を横断できる。
ただし別 tenant のデータは current tenant を切り替えない限り見えない。

## tenant スコープ権限と internal organization スコープ権限

OrgFlow では権限を 2 つのスコープに分ける。

- `TENANT_ROLE`
  tenant 全体に対して実行できる管理系操作権限
- `ROLE`
  internal organization への所属関係に対して付与される業務操作権限

この分離により、tenant 切替の対象と、同一 tenant 内の業務権限を混同しないようにする。

## この文書で固定すること / しないこと

### この文書で固定すること

- tenant と internal organization を分けること
- request / approval / approval_policy / applied_approval_route / role / audit_log の責務分離
- role は所属関係に対して付与すること
- request は申請時点の user と internal organization 帰属を直接持つこと
- audit_log は独立概念として扱うこと

### この文書でまだ固定しないこと

- internal organization の階層構造をどこまで持つか
- tenant 切り替えを UI / セッション / トークンのどこで表現するか
- 監査ログに tenant / internal organization のどの識別子を物理的に持つか
- API や OpenAPI で tenant 文脈をどの形式で受けるか
- 承認ルート設定を tenant 共通にするか internal organization ごとに持つかの細部

これらは、論理ER図・物理ER図・API設計で詰める。
