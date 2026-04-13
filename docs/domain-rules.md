# 業務ルール

この文書は、OrgFlow Phase 1 で固定して扱う業務ルールを整理するための文書です。  
ここで扱うのは、画面や API の細かな仕様ではなく、用語、境界、状態遷移、権限、承認フロー、監査の前提です。

## internal_organization_role 新名称の命名対応表

role と membership_role という用語は、業務ルールの説明において混乱を招きやすいと考え、internal_organization_role と internal_organization_membership_role に名称変更することにする。
以下に、このプロジェクト内での新名称と旧名称の対応表を示す。

| 旧名                   | 新正式名称                            | 表示名               |
| ---------------------- | ------------------------------------- | -------------------- |
| tenant_role            | tenant_role                           | tenantロール         |
| tenant_membership_role | tenant_membership_role                | tenantロール付与結果 |
| role                   | internal_organization_role            | 業務ロール           |
| membership_role        | internal_organization_membership_role | 業務ロール付与結果   |

## この文書で固定すること

- tenant / internal_organization の境界ルール
- tenant スコープ権限と internal_organization スコープ権限の分け方
- request の状態遷移
- 承認フローの完了条件
- 承認ルートをいつ確定し、どこまで保存するか
- audit_log をどういう場面で記録対象にするか

## この文書で固定しないこと

- 採用した理由や不採用案
- 概念の詳細な責務説明
- DB の物理設計
- API の request / response 形式
- current tenant をセッションやトークンでどう表すかといった実装方式

それぞれ次の文書に分けます。

- 設計理由: `docs/adr/`
- 概念の責務分担: `docs/design-overview.md`
- ER 図本体: `docs/er/`

## このアプリで扱う題材

OrgFlow は、B2B ワークフロー SaaS 風の申請・承認アプリです。  
Phase 1 では経費申請を主な題材として扱います。  
ただし、単なる金額入力フォームではなく、次の論点を含む業務アプリとして扱います。

- tenant ごとのデータ分離
- tenant 内部の部門・チーム差分
- ログイン済み利用者の操作制御
- request の状態遷移
- 承認フロー
- 監査ログ

## 最低限使う用語

### tenant

tenant は、契約主体かつデータ分離の最上位単位です。  
別 tenant のデータは、current tenant を切り替えない限り参照対象になりません。

### internal_organization

internal_organization は、tenant 内部の部門・課・チームです。  
request の帰属先、approval_policy の設定単位、業務ロールの適用単位として扱います。

### request

request は、ある時点の申請内容を表す 1 件の申請版です。  
「申請という出来事全体」ではなく、版を持つ申請データとして扱います。

### approval

approval は、request に対して行われた判断履歴です。  
現在状態そのものではなく、承認、差し戻し、却下などの判断を記録します。

### approval_policy

approval_policy は、どの条件の request にどの approval_route を適用するかを決めるルールです。

### applied_approval_route

applied_approval_route は、request 作成時に確定した承認ルートの保存結果です。

### audit_log

audit_log は、誰が・いつ・何をしたかを残す監査記録です。  
approval の代わりではなく、横断的な証跡として扱います。

## tenant / internal_organization の境界ルール

tenant はデータ分離の最上位境界です。  
tenant をまたいだ一覧取得、承認、監査ログ閲覧、設定変更は行わない前提とします。  
ある操作は、常に current tenant を前提に評価します。

internal_organization は tenant 内部の業務単位です。  
request は特定の internal_organization に帰属します。  
approval_policy も internal_organization 単位で定義します。

同一 tenant 内では、権限があれば複数 internal_organization を横断して閲覧・操作できる場合があります。  
ただし、別 tenant への横断は認めません。

## 権限スコープと管理系操作の責務分離

OrgFlow では、管理系操作を tenant スコープと internal organization スコープに分ける。

tenant スコープでは、tenant 全体の利用者・所属・tenant role を扱う。  
たとえば、user 管理、tenant membership role 管理、internal organization membership への user 追加 / 削除は tenant 管理者が担う。

一方、internal organization スコープでは、部門・課・チーム単位の業務運用を扱う。  
たとえば、approval policy、approval route、membership role の管理は internal organization 管理者が担う。

この分離により、tenant 全体管理と部門単位の業務権限管理を混同しないようにする。

## 権限ルール

権限は tenant スコープと internal_organization スコープに分けます。

tenant スコープ権限は、tenant 全体に対する管理系操作を表します。  
たとえば tenant 全体の設定変更、tenant 横断的な管理操作、広い監査ログ閲覧などが対象です。

internal_organization スコープ権限は、部門単位の業務操作を表します。  
たとえば request 作成、承認、部門単位の承認ルール設定などが対象です。

role は user に直接付与せず、所属関係に対して付与します。  
これは、同じ user でも所属先ごとに権限差分を持てるようにするためです。

役職は認可の根拠に使いません。  
部長や課長といった役職名は、承認権限や操作権限そのものとは別に扱います。

## request 作成ルール

request は、申請時点の `applicant_user_id` と `internal_organization_id` を直接持ちます。  
これは、申請時点で誰がどの internal_organization に対して申請したか、という不変の事実を保持するためです。

ただし、どの internal_organization に対して request を作成できるかは自由ではありません。  
request 作成対象として選べるのは、current tenant 内で、その user が internal_organization_membership を持ち、かつ request 作成を許可する業務ロールが付与されている internal_organization に限ります。

つまり、request 自体は申請時点の帰属を直接保持し、申請可能条件の判定は membership と role を通して行います。

## request の状態遷移ルール

Phase 1 で扱う request の状態は次の 6 つです。

- `draft`
- `in_progress`
- `approved`
- `returned`
- `rejected`
- `cancelled`

最低限認める遷移は次のとおりです。

- `draft` → `in_progress`
- `draft` → `cancelled`
- `in_progress` → `approved`
- `in_progress` → `returned`
- `in_progress` → `rejected`
- `in_progress` → `cancelled`

`draft` は下書き状態です。  
この段階では暫定入力を許容します。  
たとえば `amount = 0` のような未完成データを一時的に持つことを認めます。

`in_progress` は申請済みで承認フロー中の状態です。  
`draft` から `in_progress` へ遷移する時点では、申請として必要な入力が揃っている必要があります。

`approved`、`returned`、`rejected`、`cancelled` は終端状態です。  
終端状態に入った request は、その版としては完了したものとして扱います。

## 差し戻し後の再申請ルール

差し戻し後の再申請では、既存 request を更新して再利用しません。  
新しい request を作成します。

そのため、差し戻し前の request は `returned` のまま残ります。  
再申請後の request は別版として扱います。  
これにより、差し戻し前後の内容差分、判断履歴、監査記録を後から説明しやすくします。

## 承認フローのルール

approval は request に対する判断履歴です。  
request の現在状態そのものを持つ概念ではありません。

承認ルートは request 作成時に確定します。  
approval_policy を評価して、その結果を applied_approval_route として保存します。  
その後に approval_policy や approval_route が変更されても、既存 request の承認ルートは変えません。

同一承認段に複数承認者がいる場合、Phase 1 では **誰か 1 人が承認すればその承認段は完了する** ルールを採ります。  
全員承認が必要なケースは、Phase 1 では直接表現しません。

承認者は Phase 1 では user 直参照で扱います。  
動的な役職解決や代理承認は Phase 1 の対象外とします。

## audit_log のルール

audit_log は、request や approval の付属情報ではなく、独立した監査記録として扱います。

最低限、次のような重要操作を監査対象に含めます。

- request の作成
- request の更新
- request の申請
- 承認
- 差し戻し
- 却下
- 取消
- ログイン
- ログアウト

audit_log は、操作対象を横断的に扱います。  
そのため、特定テーブルへの単純な子関係としては扱いません。

監査の目的は、後から「誰が・どの tenant / internal_organization 文脈で・何に対して・何をしたか」を追えるようにすることです。  
したがって、単に request や approval の履歴があれば十分、とは考えません。

## この文書の読み方

この文書は、OrgFlow で何を許可し、何を固定ルールとして扱うかをまとめたものです。  
概念そのものの意味や責務は `docs/design-overview.md` を参照してください。  
採用理由や比較案は `docs/adr/` を参照してください。  
DB 制約や型は `docs/er/` を参照してください。

## 未確定の論点

Phase 1 では、次の論点はまだ固定しません。

- internal_organization の階層構造をどこまで持つか
- current tenant を UI / セッション / トークンのどこで表現するか
- 監査ログにどの識別子をどこまで物理的に持つか
- approval_policy を tenant 共通にするか internal_organization ごとに持つかの細部
- 代理申請、代理承認を扱うか
- 全員承認や主担当 / 副担当を扱うか

これらは、論理 ER 図、物理 ER 図、API 設計、実装で必要になった段階で詰めます。
