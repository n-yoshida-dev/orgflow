# docs/domain-rules.md

## 概要

この文書は、OrgFlow Phase 1 における業務ルールを整理するための文書である。

ここで扱うのは、画面やAPIの細かな仕様ではなく、次のような「設計の前提になる業務ルール」である。

- どの概念をどういう意味で分けるか
- 誰がどの操作を行うか
- request がどの状態からどの状態へ遷移するか
- approval と audit_log をどう使い分けるか
- 承認ルートをいつ確定し、どこまで保存するか
- tenant と internal organization の境界をどう扱うか

設計判断の理由そのものは ADR に残す。  
この文書では、「このアプリでは何をどう扱うか」を業務ルールとして固定する。

---

## このアプリで扱う題材

OrgFlow は、B2B ワークフロー SaaS 風の申請・承認アプリである。

Phase 1 では、経費申請を主な題材として扱う。  
ただし、単なる金額入力フォームではなく、次の論点を含む業務アプリとして扱う。

- tenant ごとのデータ分離
- tenant 内部の部門・チーム差分
- ログイン済み利用者の操作制御
- request の状態遷移
- 承認フロー
- 監査ログ

---

## このアプリで使う用語

### tenant

tenant は、契約主体かつデータ分離の最上位単位である。  
別 tenant のデータは、明示的に tenant を切り替えない限り参照できない。

例:

- 株式会社ABC
- サッカークラブ

### internal organization

internal organization は、tenant の内部にある部門・課・チームの単位である。  
同一 tenant 内では、権限があれば複数の internal organization を横断して request や audit_log を参照できる。

例:

- 人事部
- 経理部
- 開発1課

### user

user は、システムを利用する人そのものを表す。  
user は複数 tenant に所属できる。  
また、同一 tenant 内で複数の internal organization に所属できる。

### internal_organization_membership

internal_organization_membership は、user が internal organization に所属していることを表す。  
このアプリでは、role は user に直接付けず、internal_organization_membership に対して付与する。

### role

role は、操作権限を表す。  
role は「何の操作ができるか」を表し、承認ルート決定条件は持たない。

例:

- Viewer
- Approver
- Admin

### approval_policy

approval_policy は、「どの条件の request にどの承認ルートを適用するか」を決めるルールである。  
role とは別概念であり、操作権限の定義には使わない。

### request

request は、ある時点の申請内容を持つ 1 件の申請版である。  
request は「申請全体」ではなく、「系列のうちの 1 版」を表す。  
差し戻し後の再申請では、既存 request を更新せず、新しい request を作る。

### approval

approval は、request に対して行われた 1 回の判断履歴である。  
approval は現在状態そのものではない。  
現在の進行状況は request 側で管理する。

### applied_approval_route

applied_approval_route は、request 作成時に approval_policy を評価して確定した承認ルートの保存結果である。  
approval_policy や承認者マスタが後で変わっても、既存 request に適用された承認ルートは変えない。

### audit_log

audit_log は、誰が・どの tenant / internal organization で・いつ・何をしたかを記録する独立した証跡である。  
approval の代わりではなく、ログイン、設定変更、承認ルート変更なども含めて記録する。

---

## 業務上の境界

### tenant 境界

tenant はデータ分離の境界である。  
別 tenant の request、approval、audit_log、設定は、tenant を切り替えない限り参照できない。

### internal organization 境界

internal organization は、同一 tenant 内での業務上の所属単位である。  
request の帰属先、承認権限、承認ルート設定の適用範囲は internal organization を基準に扱う。

### 表示の考え方

- 承認一覧は、現在 tenant の中で、自分が承認権限を持つ internal organization の request を横断表示してよい
- 監査ログ閲覧は、現在 tenant の中で、閲覧権限がある internal organization を横断検索してよい
- ただし、別 tenant のデータは tenant 切り替えなしには見えない

---

## 利用者と権限

### 利用者の前提

- user は複数 tenant に所属できる
- user は同一 tenant 内で複数の internal organization に所属できる
- user の操作権限は、所属している internal_organization_membership ごとに変わりうる

### role の前提

- role は user に直接付与しない
- role は internal_organization_membership に対して付与する
- 同一 user でも、所属先の internal organization ごとに異なる role を持てる
- role は操作権限のみを表し、承認ルート決定条件は持たない

---

## request に関する業務ルール

### request の意味

- request は、ある時点の申請内容を持つ 1 件の申請版である
- request は申請系列全体ではない
- 同一申請の系列は別識別子で表し、各版は request として独立して持つ

### request の帰属

- request は申請時点の applicant_user と internal organization の帰属を持つ
- request は現在の internal_organization_membership への参照ではなく、申請時点の不変の事実を保持する

### request の状態

Phase 1 の request_status は次の 6 つとする。

- `draft`
- `in_progress`
- `approved`
- `returned`
- `rejected`
- `cancelled`

### request の状態遷移

Phase 1 では、最低限次の遷移を認める。

- `draft` → `in_progress`
- `draft` → `cancelled`
- `in_progress` → `approved`
- `in_progress` → `returned`
- `in_progress` → `rejected`
- `in_progress` → `cancelled`

差し戻し後の再申請は、`returned` を `in_progress` に戻すのではなく、新しい request を作ることで表現する。  
そのため、旧版 request は `returned` のまま残る。

### 金額に関する補足ルール

- `draft` では `amount = 0` を許容する
- `draft` から `in_progress` へ遷移する時点では、0 円申請は不正として扱う

---

## approval に関する業務ルール

### approval の意味

- approval は request に対する 1 回の判断履歴である
- approval は request の現在状態そのものではない

### 承認段に関するルール

- request は現在どの承認段にいるかを別途保持する
- approval は「どの承認段に対する判断か」を持つ

### 同一承認段に複数承認者がいる場合のルール

Phase 1 では、同一承認段に複数承認者がいる場合、誰か 1 人が承認した時点でその段は完了する。  
全員承認が必要な場合は、承認段を分けて順番に承認させる前提とする。

---

## approval_policy / approval_route / applied_approval_route に関する業務ルール

### approval_policy の意味

approval_policy は、「どの条件の request にどの承認ルートを適用するか」を決めるルールである。

### approval_route の意味

approval_route は、共通の承認ルート定義である。  
複数の approval_policy が同じ approval_route を参照してよい。

### applied_approval_route の意味

applied_approval_route は、request 作成時に approval_policy を評価して確定した承認ルートの保存結果である。  
既存 request の承認ルートは、後から approval_policy が変更されても変わらない。

### 保存の粒度

- 1 request に対して 1 applied_approval_route を持つ
- applied_approval_route は、ルート全体、承認段、承認者の3層で持つ

---

## audit_log に関する業務ルール

### audit_log の意味

audit_log は、操作時点の不変な証跡である。  
対象レコードが後から削除・変更されても、監査記録の意味を失わないことを優先する。

### 記録対象

Phase 1 では、少なくとも次を監査対象に含める。

- ログイン
- request 作成
- request 提出
- 承認
- 差し戻し
- 却下
- 承認ルート設定変更
- 権限変更

### 保持方針

- 操作対象は target_type と target_id で保持する
- 操作主体は ID に加えて表示用スナップショットも保持する
- audit_log は approval の代用ではない

---

## current tenant の扱い

- 別 tenant のデータは current tenant を切り替えない限り参照できない
- 承認一覧は current tenant 内で、自分が承認権限を持つ複数 internal organization の request を横断表示できる
- 監査ログ閲覧は current tenant 内で、閲覧権限がある複数 internal organization を横断検索できる
- 申請作成は current tenant 内で、申請対象 internal organization を選ぶ

## approval_policy の金額条件

Phase 1 では `approval_policy.min_amount = 0` を「金額条件なし」とみなす。

- `min_amount > 0` の場合: 指定金額以上で適用される
- `min_amount = 0` の場合: 金額条件なしで適用される

同一 `internal_organization_id` / `request_type` に対して複数候補がマッチする場合は、  
**`min_amount` が最大の policy を採用する**。

## 承認者の参照方針

Phase 1 では、共通ルート定義側・適用結果側ともに、承認者は `user_id` の直参照で表す。

- `approval_route_step_approvers.user_id`
- `applied_approval_route_step_approvers.user_id`

ただし、対象 user が current tenant に所属していることは、アプリケーション側でチェックする。

## Phase 1 で意図的に広げないこと

次は、将来の論点として扱い、Phase 1 では主対象にしない。

- 代理申請
- 代理承認
- 全員承認の直接表現
- tenant をまたぐ横断検索
- 高度な閲覧権限マトリクス
- request_type 管理UI

---

## まだ未確定の論点

- approval_policy の評価順序
- 閲覧権限の細分化
- users / tenant / internal organization の削除戦略
- 監査ログの主体側 FK をどこまで厳密に維持するか
