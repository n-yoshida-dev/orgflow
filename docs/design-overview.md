# 設計概要

この文書は、OrgFlow で扱う主要概念の責務分担を整理するための設計概要です。  
目的は、ER 図、API 設計、認可設計へ進む前に、「各概念が何を表し、どこまでを自分の責務として持つか」を固定することです。

## この文書で扱うこと

- どの概念を分けて扱うか
- 各概念が持つ責務
- 概念どうしの関係
- どの文書を正本として参照すべきか

## この文書で扱わないこと

- 誰がどの操作をしてよいかという可否判定
- request の状態遷移
- 承認完了条件
- tenant 境界をどこでチェックするかという実装上のルール
- 設計判断の採用理由
- DB 型、制約、インデックスなどの物理設計

それぞれ次の文書に委譲します。

- 業務ルール: `docs/domain-rules.md`
- 設計判断の理由: `docs/adr/`
- ER 図本体: `docs/er/`

OrgFlow は、単なる CRUD アプリではなく、申請・承認・監査・権限制御・データ分離を含む B2B ワークフロー SaaS を題材にします。  
そのため、状態、履歴、設定、権限、監査、テナント境界を 1 つの概念にまとめず、役割ごとに分けて扱います。

## 主要概念の一覧

### `tenant`

`tenant` は、契約主体かつデータ分離の最上位単位です。  
OrgFlow における「どの会社・団体のデータか」を決める境界であり、内部組織、設定、申請データの所属先となります。

### `tenant_membership`

`tenant_membership` は、ある user がある tenant に参加している、という所属関係を表します。  
user と tenant の多対多関係を表すための概念であり、tenant 単位の権限付与の基点になります。

### `tenant_role`

`tenant_role` は、tenant 全体に対する管理系操作権限を表す概念です。  
たとえば tenant 全体の設定変更、横断的な監査ログ閲覧、管理系画面へのアクセスなど、内部組織をまたぐ操作権限を表すために使います。

### `tenant_membership_role`

`tenant_membership_role` は、ある tenant_membership にどの tenant_role が付与されているかを表す中間概念です。  
tenant 全体の管理権限を、tenant への所属関係に対して付与するために置きます。

### `internal_organization`

`internal_organization` は、tenant 内部の部門・課・チームを表す概念です。  
request の帰属先、approval_policy の設定単位、業務ロールの適用単位になります。

### `internal_organization_membership`

`internal_organization_membership` は、ある tenant_membership が、tenant 内のどの internal_organization に所属しているかを表す概念です。  
部門所属という事実を表し、業務ロール付与の基点になります。

### `internal_organization_role`

`internal_organization_role` は、internal_organization 単位の業務操作権限を表す概念です。  
request 作成、承認、internal_organization 単位の設定変更など、部門単位で意味を持つ操作権限を表します。

### `internal_organization_membership_role`

`internal_organization_membership_role` は、ある internal_organization_membership にどの internal_organization_role が付与されているかを表す中間概念です。  
業務ロールを user へ直接付与するのではなく、所属関係に対して付与するために置きます。

### `user`

`user` は、システムを利用する主体です。  
tenant や internal_organization への所属、request の作成、approval の実行、audit_log の操作主体として現れます。

### `request`

`request` は、ある時点の申請内容を表す 1 件の申請版です。  
申請者、申請先 internal_organization、現在状態、申請時点で適用された承認ルートとの関係を持ちます。  
差し戻し後の再申請を既存レコードの上書きで表すのではなく、申請版として扱う前提を取ります。

### `approval`

`approval` は、ある request に対して行われた判断履歴です。  
承認、差し戻し、却下といった「判断そのもの」を表し、request の現在状態そのものは表しません。

### `approval_policy`

`approval_policy` は、どの条件の request にどの approval_route を適用するかを決めるルールです。  
申請種別や金額条件などに応じて承認ルートを選ぶための概念であり、承認の実行履歴ではありません。

### `approval_route`

`approval_route` は、承認段の並びを表すテンプレートです。  
approval_policy によって選ばれる対象であり、個別 request に直接ひもづく実行結果ではありません。

### `approval_route_step`

`approval_route_step` は、approval_route を構成する承認段です。  
何段目の承認か、という順序を表します。

### `applied_approval_route`

`applied_approval_route` は、request 作成時に確定した承認ルートの保存結果です。  
approval_policy や approval_route の後続変更が、既存 request に影響しないようにするための概念です。

### `applied_approval_route_step`

`applied_approval_route_step` は、applied_approval_route を構成する承認段です。  
request ごとに確定した承認順序を保持するために置きます。

### `applied_approval_route_step_approver`

`applied_approval_route_step_approver` は、ある applied_approval_route_step に紐づく承認者を表す概念です。  
1 つの承認段に複数承認者を持てるようにするために置きます。

### `audit_log`

`audit_log` は、誰が・どの tenant / internal_organization 文脈で・いつ・何をしたかを残す監査記録です。  
request や approval の付属情報ではなく、横断的な証跡として独立して扱います。

## 概念どうしの関係

tenant は最上位概念であり、internal_organization は tenant に属します。  
user は tenant に対して所属し、その所属関係を tenant_membership で表します。  
さらに tenant_membership は internal_organization に所属でき、その関係を internal_organization_membership で表します。

権限は 2 つのスコープに分けます。  
tenant 全体に対する管理系権限は tenant_role / tenant_membership_role で表し、internal_organization 単位の業務権限は internal_organization_role / internal_organization_membership_role で表します。  
これにより、tenant 全体管理と部門単位業務権限を混同せずに扱えます。

request は user が作成する申請版であり、internal_organization に帰属します。  
approval は request に対して行われた判断履歴です。  
approval_policy は internal_organization に属するルールであり、approval_route を選択します。  
request 作成時には、その時点で確定した結果を applied_approval_route として保存します。

audit_log は request、approval、approval_policy など特定 1 概念の子ではありません。  
複数の操作対象を横断して記録する独立概念として扱います。

## この文書と他文書の役割分担

この文書は、主要概念の責務分担と関係を固定するための文書です。  
そのため、「誰が request を作成できるか」「同一承認段で誰か 1 人の承認でよいか」「request がどの状態からどこへ遷移できるか」といった業務ルールはここでは扱いません。

それらは `docs/domain-rules.md` を正本とします。  
また、なぜその構造を採ったかという理由は `docs/adr/` を正本とします。  
主キー、外部キー、一意制約、型、インデックスは `docs/er/` を正本とします。
