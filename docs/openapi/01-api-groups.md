# 01-api-groups

## 1. 使い方

この文書は、業務操作一覧を API 群ごとに束ねた中間成果物である。
ここでは path や OpenAPI YAML はまだ確定しない。
各群について、責務・呼び出し主体・主リソース・含まれる操作・未確定論点を整理する。

---

## 2. 認証・コンテキスト群

### 2-1. この群の責務

この群は、利用者による tenant への `login` や `current tenant 切り替え` などの認証・コンテキスト操作を扱う。
ログインやテナント切り替えなどの認証に関することは含めるが、ユーザー管理などの認証以外のことは含めない。

### 2-2. 誰が呼ぶ群か

呼び出し主体は、主に利用者である。
ただし、以下のように業務操作によってログイン状態の変化がある。

- `login` は未ログインの利用者が呼ぶ。
- `current tenant 切り替え` はログイン済みの利用者が呼ぶ。

### 2-3. 主リソースを何として扱うか

この文書でいう `認証情報` は、利用者のログイン状態や現在利用中のtenantのコンテキストを指す。
また、`現在利用中のtenantのコンテキスト` は、認証済み利用者の current tenant コンテキストを指す。

### 2-4. この群に含める業務操作

| 業務操作            | 主リソース                       | メモ |
| ------------------- | -------------------------------- | ---- |
| login               | 認証情報                         | -    |
| current tenant 切替 | 現在利用中のtenantのコンテキスト | -    |

### 2-5. 次の文書で詰める論点

- collection / item / subresource / action のどれで置くか
- login, current tenant 両方とも action で置くのが自然か
- current tenant 切替の path にするときに揺れそうな点（/tenants/{tenant_id}/select のような path にするのが自然かなど）

---

## 3. request 群

### 3-1. この群の責務

この群は、requestの下書き作成や提出、requestの一覧取得や詳細表示などを扱う。
request に対する直接の操作は含めるが、request に対する承認や却下などの操作は含めない。

### 3-2. 誰が呼ぶ群か

主な呼び出し主体は `申請者` である。

### 3-3. 主リソースを何として扱うか

この文書でいう `request` は、申請者が作成したリクエストを指す。

### 3-4. この群に含める業務操作

| 業務操作                | 主リソース | メモ |
| ----------------------- | ---------- | ---- |
| request の下書き作成    | request    | -    |
| request の下書き更新    | request    | -    |
| request の下書き削除    | request    | -    |
| request の下書き提出    | request    | -    |
| request の即時提出      | request    | -    |
| request 取消            | request    | -    |
| 自分の request 一覧取得 | request    | -    |
| request の詳細表示      | request    | -    |

### 3-5. 次の文書で詰める論点

- [collection / item / subresource / action のどれで置くか]
- [一覧取得をどの単位で置くか]
- [path にするときに揺れそうな点]
- request の下書き提出と即時提出をどの単位で置くか（item で置くなら、/submit と /submit_and_approve のような path にするのが自然かなど）

---

## 4. approval 群

### 4-1. この群の責務

この群は、request に対するapprove, return, reject などの承認操作や、承認対象一覧取得の操作を扱う。

### 4-2. 誰が呼ぶ群か

主な呼び出し主体は `承認者` である。

### 4-3. 主リソースを何として扱うか

approval 群において、主リソースは `request` である。
承認操作は request に対して行われるものであるため、request を主リソースとして扱う。

### 4-4. この群に含める業務操作

| 業務操作         | 主リソース | メモ           |
| ---------------- | ---------- | -------------- |
| 承認対象一覧取得 | request    | [必要なら一言] |
| approve          | request    | [必要なら一言] |
| return           | request    | [必要なら一言] |
| reject           | request    | [必要なら一言] |

### 4-5. 次の文書で詰める論点

- [collection / item / subresource / action のどれで置くか]
- [一覧取得をどの単位で置くか]
- [path にするときに揺れそうな点]
- 承認操作のうち、approve, return, reject をどの単位で置くか（item で置くなら、/approve, /return, /reject のような path にするのが自然かなど）

---

## 5. audit 群

### 5-1. この群の責務

この群は `監査ログ一覧取得` を扱う。

### 5-2. 誰が呼ぶ群か

主な呼び出し主体は `tenant 管理者` である。

### 5-3. 主リソースを何として扱うか

この文書でいう `audit_log` は、監査ログを指す。

### 5-4. この群に含める業務操作

| 業務操作         | 主リソース | メモ |
| ---------------- | ---------- | ---- |
| 監査ログ一覧取得 | audit_log  | -    |

### 5-5. 次の文書で詰める論点

- [collection / item / subresource / action のどれで置くか]
- [一覧取得をどの単位で置くか]
- [path にするときに揺れそうな点]
- 監査ログのフィルタリング条件やソート条件をどこまで置くか

---

## 6. tenant管理群

### 6-1. この群の責務

この群は、current tenant 配下の user 管理、tenant membership 管理、および tenant_membership への tenant_role 付与 / 剥奪 / 一覧取得を扱う。  
internal_organization 単位の業務権限管理は internal_organization 管理群で扱う。

### 6-2. 誰が呼ぶ群か

主な呼び出し主体は `tenant 管理者` である。

### 6-3. 主リソースを何として扱うか

user 管理については、主リソースは `user` である。  
tenant membership に対する tenant_role 付与 / 剥奪 / 一覧取得については、主リソースは `tenant_membership` である。  
tenant_role は固定語彙であるため、主リソースとは扱わない。

### 6-4. この群に含める業務操作

| 業務操作                          | 主リソース            | メモ                                                          |
| --------------------------------- | --------------------- | ------------------------------------------------------------- |
| user作成                          | user                  | -                                                             |
| user更新                          | user                  | -                                                             |
| user削除                          | user                  | -                                                             |
| user一覧取得                      | user                  | -                                                             |
| tenantロール付与                  | tenant_membership     | tenant_role 付与先の tenant_membership を主リソースとして扱う |
| tenantロール剥奪                  | tenant_membership     | tenant_role 剥奪先の tenant_membership を主リソースとして扱う |
| tenantロール付与結果一覧取得      | tenant_membership     | -                                                             |
| internal_organizationへuser追加   | internal_organization | -                                                             |
| internal_organizationからuser削除 | internal_organization | -                                                             |

### 6-5. 次の文書で詰める論点

- [collection / item / subresource / action のどれで置くか]
- [一覧取得をどの単位で置くか]
- [path にするときに揺れそうな点]
- tenantロール付与と剥奪をどの単位で置くか（item で置くなら、/grant_role と /revoke_role のような path にするのが自然かなど）

---

## 7. internal_organization管理群

### 7-1. この群の責務

この群は、internal_organization を管理する操作、および internal_organization_membership への internal_organization_role 付与 / 剥奪 / 一覧取得を扱う。  
user 管理や tenant 全体の所属管理は tenant 管理群で扱う。

### 7-2. 誰が呼ぶ群か

主な呼び出し主体は `internal organization 管理者` である。

### 7-3. 主リソースを何として扱うか

internal_organization を管理する操作については、主リソースは `internal_organization` である。  
internal_organization_membership への role 付与 / 剥奪については、主リソースは `internal_organization_membership_role` である。  
また、一覧取得については、対象を `internal_organization` とみなし、internal_organization 配下の role 付与結果一覧として表現する。

#### 補足：internal_organization_role付与/剥奪操作 の主リソースについて

`internal_organization_role` は、internal_organization スコープの業務操作権限を表す固定語彙である。  
`internal_organization_membership_role` は、ある `internal_organization_membership` に対して、どの `internal_organization_role` が付与されているかを表す付与結果である。  
したがって、role 付与 / 剥奪 / 一覧取得という業務操作において、主リソースは `internal_organization_membership_role` として扱う。

### 7-4. この群に含める業務操作

| 業務操作                   | 主リソース                            | メモ                                                       |
| -------------------------- | ------------------------------------- | ---------------------------------------------------------- |
| internal organization作成  | internal_organization                 | -                                                          |
| internal organization更新  | internal_organization                 | -                                                          |
| internal organization削除  | internal_organization                 | -                                                          |
| 業務ロール付与             | internal_organization_membership_role | internal_organization_role の付与結果を扱う                |
| 業務ロール剥奪             | internal_organization_membership_role | internal_organization_role の付与結果を扱う                |
| 業務ロール付与結果一覧取得 | internal_organization_membership_role | internal_organization 配下の一覧として表現する可能性がある |

### 7-5. 次の文書で詰める論点

- [collection / item / subresource / action のどれで置くか]
- [一覧取得をどの単位で置くか]
- [path にするときに揺れそうな点]
- 業務ロール付与と剥奪をどの単位で置くか（item で置くなら、/grant_role と /revoke_role のような path にするのが自然かなど）

---

## 8. approval policy 管理群

### 8-1. この群の責務

この群は、approval policy を管理する操作を扱う。

### 8-2. 誰が呼ぶ群か

主な呼び出し主体は `internal organization 管理者` である。

### 8-3. 主リソースを何として扱うか

approval policy 管理群において、主リソースは `approval policy` である。

### 8-4. この群に含める業務操作

| 業務操作                 | 主リソース      | メモ |
| ------------------------ | --------------- | ---- |
| approval policy作成      | approval policy | -    |
| approval policy更新      | approval policy | -    |
| approval policy削除      | approval policy | -    |
| approval policy 一覧取得 | approval policy | -    |

### 8-5. 次の文書で詰める論点

- [collection / item / subresource / action のどれで置くか]
- [一覧取得をどの単位で置くか]
- [path にするときに揺れそうな点]
- approval policy 作成・更新・削除をどの単位で置くか（item で置くなら、/create, /update, /delete のような path にするのが自然かなど）

---

## 9. approval route管理群

### 9-1. この群の責務

この群は、approval route を管理する操作を扱う。

### 9-2. 誰が呼ぶ群か

主な呼び出し主体は `internal organization 管理者` である。

### 9-3. 主リソースを何として扱うか

approval route 管理群において、主リソースは `approval route` である。

### 9-4. この群に含める業務操作

| 業務操作                | 主リソース     | メモ |
| ----------------------- | -------------- | ---- |
| approval route作成      | approval route | -    |
| approval route更新      | approval route | -    |
| approval route削除      | approval route | -    |
| approval route 一覧取得 | approval route | -    |

### 9-5. 次の文書で詰める論点

- [collection / item / subresource / action のどれで置くか]
- [一覧取得をどの単位で置くか]
- [path にするときに揺れそうな点]
- approval route 作成・更新・削除をどの単位で置くか（item で置くなら、/create, /update, /delete のような path にするのが自然かなど）

---
