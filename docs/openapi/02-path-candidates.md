# 02-path-candidates

## 1. この文書の役割

この文書は、`01-api-groups.md` で整理した API 群ごとに、
path 候補の骨子を比較し、第一候補と未確定論点を残すための中間成果物である。

ここではまだ OpenAPI YAML の完成形は書かない。
また、request / response の詳細 schema までは確定しない。

---

## 2. 認証・コンテキスト群

### 2-1. この群で扱う範囲

この群では、以下の操作についての path 候補を整理する。

- ログイン
- 現在のtenantの切り替え

### 2-2. ログイン

| path   | 暫定method | pathの種類 | 主リソース | 気になる点                                                      |
| ------ | ---------- | ---------- | ---------- | --------------------------------------------------------------- |
| /login | POST       | action     | 認証       | POST は新規登録的な意味合いがあるが、ログインで使っていいものか |

- 第一候補: POST /login
- 理由: ログイン操作の時点では認証は行われていないため、item や collection を指定する操作は行えず、action 的な path になるのが自然。よって、ログイン操作を表すのに適した path は /login となる。
- 主要4xx:
  - 401:アカウントが存在しない、パスワードが間違っているなどの未認証に当たるケース
  - 403:アカウントは存在するが、ログインが禁止されているなどの権限不足に当たるケース
  - 422:login での入力値不正に当たるケース
- 未確定な点: ログイン方法（メールアドレス直接入力、Googleログイン、Appleログインなど）毎に path を分ける必要があるかどうか。

### 2-3. current tenant切り替え

| path                       | 暫定method | pathの種類 | 主リソース | 気になる点                                                                                                                                      |
| -------------------------- | ---------- | ---------- | ---------- | ----------------------------------------------------------------------------------------------------------------------------------------------- |
| /tenants/{tenantId}        | POST       | item       | tenant     | POST は新規登録的な意味合いがあるが、tenant切り替えで使っていいものか, id だけが渡される場合は、tenant 情報への操作(編集、削除など)に見えないか |
| /tenants/{tenantId}/select | POST       | action     | tenant     | POST は新規登録的な意味合いがあるが、tenant切り替えで使っていいものか                                                                           |

- 第一候補: POST /tenants/{tenantId}/select
- 理由: 指定した tenant への切り替え操作であることが分かりやすいため。また、/tenants/{tenantId} は tenant 情報への操作(編集、削除など)に見える可能性があるため、/tenants/{tenantId}/select の方が適していると考えられる。
- 主要4xx:
  - 401:未ログインでのtenant切り替えなどの未認証に当たるケース
  - 404:存在しないtenantへの切り替えなど、切り替え先が見つからないケース / 所属していないtenantへの切り替えなどの権限不足に当たるケース
- 未確定な点: 今の段階では「404 に寄せる案を第一候補とする」が 403 / 404 を切り分けて考えるかは方針未定確定とする。tenant切り替えの方法（/tenants/{tenantId} にするか /tenants/{tenantId}/select にするか）について、POST を使うことに問題がないかどうか。

---

## 3. request 群

### 3-1. この群で扱う範囲

この群では、request を主リソースとする endpoint 群の path 候補を整理する。
特に、request の collection / item、状態遷移系の action、一覧取得の見せ方を検討対象とする。

### 3-2. request collection / item の置き方

#### 3-2-1. この小節で比較したいこと

- request の作成・一覧取得・詳細取得・更新・削除を、どの path family で受けるか
- draft を request の一状態として扱うか、draft 専用 path を切るか

#### 3-2-2. path 候補

| ラベル | path                         | 暫定method   | pathの種類 | 主リソース | 気になる点                                                                                                                                                                                                                                   |
| ------ | ---------------------------- | ------------ | ---------- | ---------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 案A    | /requests/drafts             | POST         | collection | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests/drafts             | GET          | collection | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests/drafts/{requestId} | GET          | item       | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests/drafts/{requestId} | PUT or PATCH | item       | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests/drafts/{requestId} | DELETE       | item       | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests                    | POST         | collection | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests                    | GET          | collection | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests/{requestId}        | GET          | item       | request    | -                                                                                                                                                                                                                                            |
| 案A    | /requests/{requestId}        | PUT or PATCH | item       | request    | -                                                                                                                                                                                                                                            |
| 案B    | /requests/drafts             | POST         | collection | request    | request 提出時、draft ラベルを付ける旨を form 内で選択すれば、drafts という path を切らなくてもよいきがする。しかし、下書きボタンによるdraftの作成は、明示的な path があるとわかりやすいか？                                                 |
| 案B    | /requests/drafts/{requestId} | DELETE       | item       | request    | 下書きの場合の取消は物理データ削除の方針だったはずなので 下書きcancel と区別するために drafts というpath を切ってみたが、path を切らずとも、データ内に draft であることを示すラベルがあるならば、DELETE /requests/{requestId} で対応可能か？ |
| 案B    | /requests                    | POST         | collection | request    | -                                                                                                                                                                                                                                            |
| 案B    | /requests                    | GET          | collection | request    | 下書きと提出済みの request を区別して一覧取得したい場合はどうすればよいか                                                                                                                                                                    |
| 案B    | /requests/{requestId}        | GET          | item       | request    | /requests/{requestId} は draft も submitted も同じ request item として扱う前提か                                                                                                                                                             |
| 案B    | /requests/{requestId}        | PUT or PATCH | item       | request    | /requests/{requestId} は draft も submitted も同じ request item として扱う前提か                                                                                                                                                             |
| 案C    | /requests                    | POST         | collection | request    | draft を作成する場合は query で指定する                                                                                                                                                                                                      |
| 案C    | /requests                    | GET          | collection | request    | draft を検索する場合は query で指定する                                                                                                                                                                                                      |
| 案C    | /requests/{requestId}        | GET          | item       | request    | draft を検索する場合は query で指定する                                                                                                                                                                                                      |
| 案C    | /requests/{requestId}        | PATCH        | item       | request    | -                                                                                                                                                                                                                                            |
| 案C    | /requests/{requestId}        | DELETE       | item       | request    | -                                                                                                                                                                                                                                            |
| 案C    | /requests/{requestId}/submit | POST         | item       | request    | 下書きを提出する                                                                                                                                                                                                                             |
| 案C    | /requests/{requestId}/cancel | POST         | item       | request    | 下書きを取消する                                                                                                                                                                                                                             |

#### 3-2-3. 第一候補

- 第一候補: 案C: draft を request の一状態として扱い、draft 専用 path を切らない案
- 理由:
  - draft は別リソースではなく request の状態の一つであるため、draft 専用 path を切らずに、request item の属性で draft であることを示す方が自然であると考えられるため

#### 3-2-4. 主要4xx

- 401: 未ログインの状態での request 作成・取得・更新・削除などを行い、未認証に当たるケース
- 403: 申請権限のない人が request 作成・取得・更新・削除などを行うなどの権限不足に当たるケース
- 404: 存在しない request への操作など、対象が見つからないケース / 他人の request への操作などの権限不足操作において、対象の存在を認知させたくないケース
- 409: draft ではない request を 提出または削除しようとしたなどの、item はあるが、今の状態ではその操作を受けないケース
- 422: request 作成時の入力値が不正なケース

#### 3-2-5. 未確定な点

- /requests/{requestId} の更新を PUTとするかPATCH とするか（request 更新を全体置換と見るのか、部分更新と見るのか）
- 一覧取得を GET /requests 1 本でフィルタリングするか、draft 専用一覧を別に切るか

### 3-3. request の状態遷移系 action の置き方

#### 3-3-1. この小節で比較したいこと

- submit / cancel などを item 更新として扱うか、action path として扱うか

#### 3-3-2. path 候補

| ラベル | path                         | 暫定method | pathの種類 | 主リソース | 気になる点                                          |
| ------ | ---------------------------- | ---------- | ---------- | ---------- | --------------------------------------------------- |
| 案A    | /requests/drafts/{requestId} | POST       | item       | request    | 案Aは method の意味だけで状態遷移を表そうとする案   |
| 案A    | /requests/{requestId}        | DELETE     | item       | request    | 案Aは method の意味だけで状態遷移を表そうとする案   |
| 案B    | /requests/{requestId}/submit | POST       | action     | request    | -                                                   |
| 案B    | /requests/{requestId}/cancel | POST       | action     | request    | -                                                   |
| 案C    | /requests/drafts/{requestId} | PATCH      | item       | request    | 案Cは request item の属性更新として状態遷移を表す案 |
| 案C    | /requests/{requestId}        | PATCH      | item       | request    | 案Cは request item の属性更新として状態遷移を表す案 |

#### 3-3-3. 第一候補

- 第一候補: 案B: draft提出 と requestキャンセル を action 的な path で明確に表見する案
- 理由:
  - API利用者へ、エンドポイントからどのような状態遷移操作が行われるかを判断しやすくするため
  - 案A の場合、DELETE /requests/{requestId} はAPI利用者からみると request削除 にみえてしまうため（行いたいのは削除ではなく canceled への状態遷移）
  - 案A の場合、POST /requests/drafts/{requestId} は item に対するどのような action なのか読み取り辛いため
  - 案C の場合、API利用者からみるとどのような更新なのかの判断がしづらいため

#### 3-3-4. 主要4xx

- 401: 未ログイン状態での状態遷移系操作を行い、未認証に当たるケース
- 404: 存在しない request への操作など、対象が見つからないケース / 他人の request への操作などの権限不足操作において、対象の存在を認知させたくないケース
- 409: request の状態が、下書き投稿やcancelなどの操作を受け付けられない状態であるケース
- 422: submit 時に必須項目が未入力などの不正なケース

#### 3-3-5. 未確定な点

- action path を採る場合、submit と cancel 以外の状態遷移も同じ方針で揃えるか

---

## 4. approval 群

### 4-1. この群で扱う範囲

この群では、approval を主題とする endpoint 群の path 候補を整理する。
特に、承認対象一覧の見せ方と、approve / return / reject などの判断操作の置き方を検討対象とする。

### 4-2. 承認対象一覧の見せ方

| ラベル | path                             | 暫定method | pathの種類 | 主リソース | 気になる点 |
| ------ | -------------------------------- | ---------- | ---------- | ---------- | ---------- |
| 案A    | /approvals                       | GET        | collection | request    | -          |
| 案B    | /requests/approvals              | GET        | collection | request    | -          |
| 案C    | /requests?scope=pending-approval | GET        | collection | request    | -          |

- 第一候補: 案C: /requests?scope=pending-approval という path で、承認対象の request 一覧を表現する案
- 理由:
  - approval は rquest に紐づくリソースであるため、path に requests を使用し、承認対象の絞り込みは query で行う方が自然であると考えられるため
- 主要4xx:
  - 401: 未ログイン状態での一覧取得作を行い、未認証に当たるケース
  - 403: 承認権限のない人が承認一覧取得を行うなどの権限不足に当たるケース
- 未確定な点:
  - 他に候補はないか

### 4-3. 承認判断操作の置き方

| ラベル | path                          | 暫定method | pathの種類 | 主リソース | 気になる点 |
| ------ | ----------------------------- | ---------- | ---------- | ---------- | ---------- |
| 案A    | /requests/{requestId}/approve | POST       | action     | request    | -          |
| 案A    | /requests/{requestId}/return  | POST       | action     | request    | -          |
| 案A    | /requests/{requestId}/reject  | POST       | action     | request    | -          |

- 第一候補: 案A: requests を主リソースとして、requests の item に対する操作を path で表現する案
- 理由:
  - API利用者からすると、path から操作内容を推測しやすい
- 主要4xx:
  - 401: 未ログイン状態での承認系操作を行い、未認証に当たるケース
  - 403: 承認権限のない人がrequest 承認・差し戻し・却下などを行うなどの権限不足に当たるケース
  - 404: 存在しない request への操作など、対象が見つからないケース / 承認対象外の request への操作などの権限不足操作において、対象の存在を認知させたくないケース
  - 409: request の状態が draft や returned, canceled などで、承認系操作を受け付けられない状態であるケース
  - 422: 承認系操作時のコメント入力などで、入力値が不正などのケース
- 未確定な点:
  - 案A以外に候補はないか

---

## 5. audit 群

### 5-1. この群で扱う範囲

この群では、audit log の閲覧系 endpoint の path 候補を整理する。

### 5-2. audit 一覧 / 詳細の見せ方

| path                     | 暫定method | pathの種類 | 主リソース | 気になる点 |
| ------------------------ | ---------- | ---------- | ---------- | ---------- |
| /audit-logs              | GET        | collection | audit_log  | -          |
| /audit-logs/{auditLogId} | GET        | item       | audit_log  | -          |

- 第一候補: /audit-logs という path で、audit log の一覧と詳細を表現する案
- 理由:
  - 監査ログ閲覧は特定の request や approval に紐づかないため、単独の path として定義する
- 主要4xx:
  - 401: 未ログイン状態での監査ログ閲覧操作を行い、未認証に当たるケース
  - 403: 監査ログ閲覧権限のない人が操作を行うなどの権限不足に当たるケース
- 未確定な点:
  - 検索条件はqueryによって指定すると想定し、path は audit_log だけにしたが、他に操作候補はあるか

---

## 6. tenant管理群

### 6-1. この群で扱う範囲

この群では、tenant スコープの管理系 endpoint 群の path 候補を整理する。
特に、tenant 自体、user、tenant membership role の見せ方を検討対象とする。

### 6-2. tenant / user collection・item の置き方

| ラベル | path                | 暫定method | pathの種類 | 主リソース | 気になる点                        |
| ------ | ------------------- | ---------- | ---------- | ---------- | --------------------------------- |
| 案A    | /tenants/{tenantId} | DELETE     | item       | tenant     | tenant 作成に関する path は必要か |
| 案A    | /users              | POST       | collection | user       | -                                 |
| 案A    | /users/{userId}     | PATCH      | item       | user       | -                                 |
| 案A    | /users/{userId}     | DELETE     | item       | user       | -                                 |
| 案A    | /users/{userId}     | GET        | item       | user       | -                                 |
| 案A    | /users              | GET        | collection | user       | -                                 |

- 第一候補: 案A: tenant と user をそれぞれ独立した collection / item path で表現する案
- 理由:
  - tenant削除やuser作成・取得・更新・削除といった操作は単純なCRUDであるため、collection / item path で表現するのが自然であると考えた
- 主要4xx:
  - 401: 未ログイン状態でのCRUD操作を行い、未認証に当たるケース
  - 403: tenant管理者権限が無い状態でのCRUD操作を行うなどの権限不足に当たるケース
  - 404: 存在しない tenant や user への操作など、対象が見つからないケース / 他の tenant の user への操作などの権限不足操作において、対象の存在を認知させたくないケース
  - 409: 削除済みのuserへのCRUD操作など、受け付けられない状態であるケース
  - 422: user作成や更新時において、入力値が不正などのケース
- 未確定な点:
  - tenant への操作が、削除以外に必要かどうか
  - user の更新を PUT とするか PATCH とするか（user 更新を全体置換と見るのか、部分更新と見るのか）

### 6-3. tenant membership role の置き方

| ラベル | path                                                            | 暫定method | pathの種類  | 主リソース        | 気になる点                                                           |
| ------ | --------------------------------------------------------------- | ---------- | ----------- | ----------------- | -------------------------------------------------------------------- |
| 案A    | /tenant-memberships/{tenantMembershipId}/roles/                 | GET        | collection  | tenant_membership | -                                                                    |
| 案A    | /tenant-memberships/{tenantMembershipId}/roles/                 | POST       | collection  | tenant_membership | role は "付与" されるというイメージだが、POST で表現して問題ないか   |
| 案A    | /tenant-memberships/{tenantMembershipId}/roles/{tenantRoleId}   | DELETE     | item        | tenant_membership | role は "剥奪" されるというイメージだが、DELETE で表現して問題ないか |
| 案B    | /tenant-memberships                                             | GET        | collection  | tenant_membership | -                                                                    |
| 案B    | /tenant-memberships/{tenantMembershipId}/add-role               | POST       | subresource | tenant_membership | -                                                                    |
| 案B    | /tenant-memberships/{tenantMembershipId}/roles/{id}/delete-role | POST       | subresource | tenant_membership | -                                                                    |

- 第一候補: 案A: tenant role は、user 情報に直接付与されるのではなく、tenant_membership というリソースに対して付与されるため、path に users は不要であり、tenant-memberships という path で表現する案
- 理由:
  - tenant_membership というリソースに対して、tenant_role の付与・剥奪が行われるため、path に users を入れるのは冗長であると考えられるため
- 主要4xx:
  - 401: 未ログイン状態でのCRUD操作を行い、未認証に当たるケース
  - 403: tenant管理者権限が無い状態でのrole付与・剥奪操作を行うなどの権限不足に当たるケース
  - 404: 存在しない tenant_membership への操作など、対象が見つからないケース / 他の tenant の tenant_membership への操作などの権限不足操作において、対象の存在を認知させたくないケース
  - 409: すでに role が付与されている tenant_membership への role 付与操作など、受け付けられない状態であるケース
- 未確定な点:
  - tenant-memberships という path に対し、role 付与・剥奪以外に操作は必要か
  - tenant-memberships という path 名は長すぎるかどうか

---

## 7. internal_organization管理群

### 7-1. この群で扱う範囲

この群では、internal organization スコープの管理系 endpoint 群の path 候補を整理する。
特に、internal organization 自体、membership、internal organization membership role の見せ方を検討対象とする。

### 7-2. internal organization / membership の置き方

| ラベル | path                                                                  | 暫定method | pathの種類  | 主リソース             | 気になる点                                                              |
| ------ | --------------------------------------------------------------------- | ---------- | ----------- | ---------------------- | ----------------------------------------------------------------------- |
| 案A    | /internal-organizations                                               | POST       | collection  | internal-organizations | -                                                                       |
| 案A    | /internal-organizations/{internalOrganizationId}                      | PATCH      | item        | internal-organizations | -                                                                       |
| 案A    | /internal-organizations/{internalOrganizationId}                      | DELETE     | item        | internal-organizations | -                                                                       |
| 案A    | /internal-organizations/{internalOrganizationId}                      | GET        | item        | internal-organizations | -                                                                       |
| 案A    | /internal-organizations                                               | GET        | collection  | internal-organizations | -                                                                       |
| 案A    | /internal-organization-memberships/                                   | POST       | collection  | internal-organizations | path が長すぎないか？                                                   |
| 案A    | /internal-organization-memberships/{internalOrganizationMembershipId} | DELETE     | item        | internal-organizations | path が長すぎないか？                                                   |
| 案B    | /internal-organizations/{id}/add-user                                 | POST       | subresource | internal-organizations | membership の指定をしないと organization と user の関連が不明確になるか |
| 案B    | /internal-organizations/{id}/delete-user                              | POST       | subresource | internal-organizations | membership の指定をしないと organization と user の関連が不明確になるか |

※案Bに関しては、membership についてのみ記載。membership 以外は案Aと同様。

- 第一候補: 案A: internal organization と membership をそれぞれ独立した collection / item path で表現する案
  - 理由: 案Bの場合、internal organization への user 追加・削除は、内部的には membership の作成・削除であるが、API利用者から見ると、internal organization に対する操作に見えるため、path に users を入れるのは冗長であると考えられるため
- 主要4xx:
  - 401: 未ログイン状態でのCRUD操作を行い、未認証に当たるケース
  - 403: organization管理者権限またはtenant管理者権限が無い状態でのCRUD操作を行うなどの権限不足に当たるケース
  - 404: 存在しない internal_organization または internal_organization_membership への操作など、対象が見つからないケース
  - 422: リクエストボディの形式が不正なケース
- 未確定な点:
  - internal organization への操作が、削除以外に必要かどうか
  - internal-organization-memberships という path 名は長すぎるかどうか

### 7-3. internal organization membership role の置き方

| ラベル | path                                                                                                     | 暫定method | pathの種類 | 主リソース                        | 気になる点                    |
| ------ | -------------------------------------------------------------------------------------------------------- | ---------- | ---------- | --------------------------------- | ----------------------------- |
| 案A    | /internal-organization-memberships/{internalOrganizationMembershipId}/roles                              | GET        | collection | internal_organization_memberships | 全体的に、path 名が長くないか |
| 案A    | /internal-organization-memberships/{internalOrganizationMembershipId}/roles                              | POST       | collection | internal_organization_memberships | -                             |
| 案A    | /internal-organization-memberships/{internalOrganizationMembershipId}/roles/{internalOrganizationRoleId} | DELETE     | item       | internal_organization_memberships | -                             |

- 第一候補: 案A: internal-organization-memberships に対し、collection / item path で表現する案
- 理由:
  - path がシンプルになるため。
  - CRUDで表現できる操作であるため。
- 主要4xx:
  - 401: 未ログイン状態でのCRUD操作を行い、未認証に当たるケース
  - 403: internal organization管理者権限が無い状態でのrole付与・剥奪操作を行うなどの権限不足に当たるケース
  - 404: 存在しない internal_organization_membership への操作など、対象が見つからないケース
  - 409: 重複したrole付与 など、リソースの状態が不正なケース
  - 422: リクエストボディの形式が不正なケース
- 未確定な点:
  - internal-organization-memberships という path に対し、role 付与・剥奪以外に操作は必要か
  - internal-organization-memberships という path 名は長すぎるかどうか

---

## 8. approval policy 管理群

### 8-1. この群で扱う範囲

この群では、approval policy の管理系 endpoint 群の path 候補を整理する。

### 8-2. approval policy collection / item の置き方

| ラベル | path                                                               | 暫定method   | pathの種類 | 主リソース        | 気になる点 |
| ------ | ------------------------------------------------------------------ | ------------ | ---------- | ----------------- | ---------- |
| 案A    | /approval-policies                                                 | POST         | collection | approval_policies | -          |
| 案A    | /approval-policies/{id}                                            | GET          | item       | approval_policies | -          |
| 案A    | /approval-policies                                                 | GET          | collection | approval_policies | -          |
| 案A    | /approval-policies/{id}                                            | PUT or PATCH | item       | approval_policies | -          |
| 案A    | /approval-policies/{id}                                            | DELETE       | item       | approval_policies | -          |
| 案B    | /internal-organizations/{internalOrganizationId}/approval-policies | POST         | collection | approval_policies | -          |
| 案B    | /internal-organizations/{internalOrganizationId}/approval-policies | GET          | collection | approval_policies | -          |
| 案B    | /approval-policies/{approvalPolicyId}                              | GET          | item       | approval_policies | -          |
| 案B    | /approval-policies/{approvalPolicyId}                              | PATCH        | item       | approval_policies | -          |
| 案B    | /approval-policies/{approvalPolicyId}                              | DELETE       | item       | approval_policies | -          |

- 第一候補: 案B: collection だけ internal organization に紐づく path にして、item は approval policy 単独の path にする案
- 理由:
  - approval policy の作成・取得・更新・削除といった操作は単純なCRUDであるため、collection / item path で表現するのが自然であると考えた
- 主要4xx:
  - 401: 未ログイン状態でのCRUD操作を行い、未認証に当たるケース
  - 403: internal organization管理者権限が無い状態でのCRUD操作を行うなどの権限不足に当たるケース
  - 404: 存在しない approval_policy への操作など、対象が見つからないケース
  - 409: 重複したapproval policy の作成など、リソースの状態が不正なケース
  - 422: リクエストボディの形式が不正なケース
- 未確定な点:
  - approval-policies という path 名は長すぎるかどうか
  - approval policy への操作が、CRUD以外に必要かどうか

---

## 9. approval route管理群

### 9-1. この群で扱う範囲

この群では、approval route の管理系 endpoint 群の path 候補を整理する。

### 9-2. approval route collection / item の置き方

| ラベル | path                               | 暫定method | pathの種類 | 主リソース      | 気になる点 |
| ------ | ---------------------------------- | ---------- | ---------- | --------------- | ---------- |
| 案A    | /approval-routes                   | POST       | collection | approval_routes | -          |
| 案A    | /approval-routes/{approvalRouteId} | GET        | item       | approval_routes | -          |
| 案A    | /approval-routes                   | GET        | collection | approval_routes | -          |
| 案A    | /approval-routes/{approvalRouteId} | PATCH      | item       | approval_routes | -          |
| 案A    | /approval-routes/{approvalRouteId} | DELETE     | item       | approval_routes | -          |

- 第一候補: 案A: approval route を approval-routes という collection / item path で表現する案
- 理由:
  - approval route の作成・取得・更新・削除といった操作は単純なCRUDであるため、collection / item path で表現するのが自然であると考えた
- 主要4xx:
  - 401: 未ログイン状態でのCRUD操作を行い、未認証に当たるケース
  - 403: internal organization管理者権限が無い状態でのCRUD操作を行うなどの権限不足に当たるケース
  - 404: 存在しない approval_route への操作など、対象が見つからないケース
  - 409: 重複したapproval route の作成など、リソースの状態が不正なケース
  - 422: リクエストボディの形式が不正なケース
- 未確定な点:
  - approval-routes という path 名は長すぎるかどうか
  - approval route への操作が、CRUD以外に必要かどうか
