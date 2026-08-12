# ADR-024: tenant 選択状態を JWT の `current_tenant_id` claim で表現する

- Status: Accepted
- Date: 2026-06-04（`GET /me/tenants` の扱いを含めた確定は 2026-06-19）
- 関連: ADR-013（tenant と internal organization の分離） / ADR-015（tenant_id による分離） / ADR-022（JWT の発行と検証） / ADR-023（sub の利用）

> **決定**: ログイン直後の JWT には `current_tenant_id` を含めない。`POST /tenants/{tenantId}/select` で membership を確認し、成功した場合に `current_tenant_id` 入り JWT を再発行する。tenant スコープ API はこの claim を前提とし、`GET /me/tenants` は前提としない。

| 状況 | HTTP | 外部向けメッセージ |
|---|---|---|
| tenant 選択時に membership が存在しない | 404 | 対象のtenantが見つかりません |
| tenant スコープ API で claim が欠落 | 403 | tenantが未選択です |
| claim が UUID として解釈できない | 401 | 認証情報が不正です |

## 背景

OrgFlow はマルチテナント構成のため、認証済み userId が確定しただけでは、後続の業務 API をどの tenant 上で実行するかが決まらない。

ログイン後に user が所属 tenant を選択し、その選択結果を後続 API の認可判定へ渡す仕組みが必要だった。あわせて、tenant 未選択のまま tenant スコープ API を呼ばれたときのレスポンスを決める必要があった。

## 理由

- ログイン時点では操作対象 tenant が確定していない。`current_tenant_id: null` を入れるより claim 自体を含めないほうが、「未選択」という状態を素直に表せる
- token に持たせることで、tenant スコープ API が毎回 body や query parameter から tenantId を受け取らずに済む。tenant 境界の判定材料が1箇所に揃う
- 再発行時に membership を確認するため、所属していない tenant を指す token は発行されない
- claim の欠落と UUID 不正では意味が違う。欠落は認証が成立したうえでの未選択なので 403、UUID 不正は認証情報そのものが壊れているので 401
- membership がない場合に 404 とし、tenant 自体が存在しない場合と区別しないことで、他 tenant の存在を外部から推測されない
- `sub` と `current_tenant_id` は「誰か」と「どこで操作するか」で意味が違うため、取り出す部品を分ける
- `GET /me/tenants` は tenant を選択する前段の導線であり、claim を必須にすると選択そのものができなくなる。所属 0 件は異常ではないため、例外ではなく 200 と空配列を返す

## 検討した他の案

- **案A: `/login` 時点で `current_tenant_id: null` を入れる** — claim が存在して値が null という状態になり、必須なのか任意なのかが読み取れない
- **案B: `/login` 時点でデフォルト tenant を自動選択する** — 複数所属時にどれを選ぶかのルールが別途必要になり、利用者が選択する画面導線とも合わない
- **案C: tenantId を毎回 request body / query parameter で渡す** — 各 API で受け渡しが重複し、tenant 境界の判定が散らばる
- **案D: claim 欠落を 401 にする** — token 自体は有効で認証は成立しているため、認証失敗を示す 401 は誤り
- **案E: claim 欠落を 404 にする** — 404 は tenant の存在を隠すために使う。今回は tenant の存在有無ではなく選択状態の問題
- **案F: claim の UUID 不正を 400 にする** — 400 は body や path parameter などクライアント入力の形式不正に寄せる。壊れているのは認証情報である claim
- **案G: `GET /me/tenants` で userId をクライアントから受け取る** — 認証済み user 自身の一覧を返す API であり、`sub` から決定できる

## 受け入れる制約

- `current_tenant_id` を必要とする API と必要としない API を分けて管理する必要がある。Phase 1 では `POST /tenants/{tenantId}/select` と `GET /me/tenants` が不要側
- tenant を切り替えると token を再発行するが、切替前の token も有効期限までは使える。古い token を持っていれば前の tenant で操作できる時間が残る
- tenant スコープ API を実装するたびに、userId と currentTenantId を Controller から Service へどう渡すかを個別に扱う必要がある
- `GET /me/tenants` が返すのは tenantId と tenantName のみ。role や最終選択日時は含めない
- tenant role や internal organization の権限は claim に含めない

## 見直す条件

- tenant スコープ API が増え、Controller で毎回同じ取り出し処理を書く重複が目立つ場合
- tenant role や権限情報を claim に含める必要が出た場合
- tenant 選択状態を JWT ではなくサーバー側セッションや DB で保持する設計へ変更する場合
- tenant 一覧に role・最終選択日時・利用停止状態などを返す必要が出た場合
- 外部 IdP / OIDC、refresh token を導入する場合
