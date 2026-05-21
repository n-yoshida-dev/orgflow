# OrgFlow

OrgFlow は、申請・承認フローを題材にした業務ワークフローアプリケーションです。

経費申請を中心とした **申請 → 承認 → 監査ログ記録** の流れを題材に、REST API、DB 設計、認証・認可、RBAC、監査ログ、OpenAPI、テスト、CI などを段階的に実装・整理することを目的としています。

## このリポジトリの目的

このリポジトリの目的は、単に CRUD API を作ることではありません。

申請・承認のような状態遷移を伴う業務フローを、データ構造、API、認証・認可、監査ログと結びつけて整理し、後から設計意図を説明できる状態にすることを目的としています。

主に以下を重視します。

- 業務フローをデータ構造と API に落とし込むこと
- 認証と認可を分けて扱うこと
- ロールごとの操作権限を整理すること
- 監査ログを後付けではなく設計対象として扱うこと
- API 契約を OpenAPI として管理すること
- 実装判断を README / docs / ADR に残すこと

## 現在の状態

現在は Phase 1 の途中で、設計資料と OpenAPI 契約を前提に、Spring Boot API の実装を進めている段階です。

現時点では、主に以下を整理・実装しています。

- 業務ルールの文章化
- 設計方針の整理
- ADR の作成
- 概念 ER 図 / 論理 ER 図 / 物理 ER 図の整理
- OpenAPI による API 契約の整理
- Spring Boot API プロジェクトの作成
- Docker Compose による PostgreSQL 起動
- Flyway による DB migration / seed データ管理
- Spring Security の最小設定
- DB 上の `users` を使った loginId / password 認証
- BCrypt hash を使った password 照合
- `/login` 成功時の JWT access token 発行
- Spring Security Resource Server による Bearer token 検証
- JWTなし / 正常JWT / 改ざんJWT / 期限切れJWT の Postman確認
- `@Valid` 失敗時に 422 を返す例外ハンドリング

この README は「完成済みアプリケーションの利用手順」ではなく、**現在の到達点とドキュメントの入口**として記載しています。

## 題材として申請・承認フローを扱う理由

OrgFlow では、単純な ToDo ではなく、申請・承認フローを題材にしています。

理由は、以下の論点を 1 つの題材で扱いやすいからです。

- tenant をまたぐデータ分離
- tenant 内の internal organization ごとの差分
- ロールと認可
- 承認フロー
- 状態遷移
- 監査ログ
- 設計判断の記録

## 現時点の主要ドキュメント

### GitHub Pages

- 公開ドキュメント入口
  - `https://n-yoshida-dev.github.io/orgflow/`

### 設計関連

- [Docs 入口](./docs/index.md)
- [設計概要](./docs/design-overview.md)
- [業務ルール](./docs/domain-rules.md)

### ER 関連

- [ER 設計メモ](./docs/er/00_notes.md)
- [概念 ER 図](./docs/er/01_concept-er.md)
- [論理 ER 図（DBML）](./docs/er/02_logical-er.dbml)
- [物理 ER 図（DBML）](./docs/er/03_physical-er.dbml)

### OpenAPI 関連

- [業務操作カタログ](./docs/openapi/00-operation-catalog.md)
- [API グループ整理](./docs/openapi/01-api-groups.md)
- [path 候補整理](./docs/openapi/02-path-candidates.md)
- [OpenAPI 定義](./docs/openapi/openapi.yaml)

### 実装対応表

- [実装対応表](./docs/design/implementation-mapping.tsv)

### ADR

- [ADR 一覧](./docs/adr/index.md)

## 現時点での設計ドキュメントの見方

README には設計詳細を詰め込まず、全体像の入口だけを置いています。

設計の詳細は `docs/` と `ADR` に分けて管理しています。

- 概念の責務分担: `./docs/design-overview.md`
- 固定する業務ルール: `./docs/domain-rules.md`
- ER 図の補足・概念図・論理図・物理図: `./docs/er/`
- API 契約: `./docs/openapi/openapi.yaml`
- 実装対応表: `./docs/design/implementation-mapping.tsv`
- 設計判断の理由: `./docs/adr/`
- ドキュメント全体の入口: `./docs/index.md`

## Phase 1 の到達目標

Phase 1 では、Java / Spring Boot による OrgFlow API v1 を対象に、以下を説明できる状態を目指しています。

- tenant / internal organization / membership の意味
- tenant スコープ権限と internal organization スコープ権限の違い
- request / approval / approval_policy / applied_approval_route / audit_log の意味
- 認証済み API
- 認証と認可の違い
- Spring Security による粗い認可
- Service / Policy 側で行う細かい認可
- RBAC
- 申請 → 承認または差し戻し → 監査ログ記録 の一貫した業務フロー
- OpenAPI による API 契約
- 単体テスト / 統合テスト
- GitHub Actions CI

## 想定技術スタック（Phase 1）

- Java 21
- Spring Boot
- Spring Security
- Spring Security OAuth2 Resource Server
- Spring Security OAuth2 JOSE
- Spring Data JPA
- PostgreSQL
- Flyway
- OpenAPI
- Docker Compose
- GitHub Actions

※ 現時点では、技術選定の一部は設計・検証段階を含みます。
※ 実装済み範囲と今後の予定は、進行に合わせて README と docs を更新します。

## 現在の実装状況

現時点では、OrgFlow API の認証系最小土台を実装中です。

完了済みの内容は次のとおりです。

- Spring Boot API プロジェクトを `api/` 配下に作成
- Docker Compose で PostgreSQL を起動
- `application.yaml` に PostgreSQL 接続設定を追加
- Flyway により `users` テーブルと seed user を作成
- Spring Security の最小設定を追加
  - `/login`、`/hello`、`/error` は `permitAll`
  - それ以外は `authenticated`
  - CSRF 無効
  - セッションは stateless
  - Resource Server として JWT を検証
- `/login` 用の request / response DTO を作成
  - `LoginRequest`: `loginId`, `password`
  - `LoginResponse`: `accessToken`, `currentTenantId`
- `AuthController` を作成
- `AuthService` を作成し、Controller から Service を呼ぶ形に分離
- `User` Entity を作成
- `UserRepository` を作成
- `PasswordEncoder` による BCrypt password 照合を実装
- 認証失敗時に 401 を返す `AuthenticationFailedException` と `GlobalExceptionHandler` を作成
- `@Valid` 失敗時に 422 を返す `GlobalExceptionHandler` を作成
- JWT 設定を `application.yaml` で管理
  - `jwt.issuer`
  - `jwt.secret`
  - `jwt.access-token-expires-in`
- `JwtProperties` を作成
- `JwtConfig` を作成
  - `JwtEncoder`
  - `JwtDecoder`
- `JwtTokenService` を作成
- `/login` 成功時に JWT access token を発行
- `GET /hello-auth` を検証用の保護APIとして追加
- Postman で JWT 検証の動作を確認
- `mvn test` が `BUILD SUCCESS`

## 認証まわりの現在仕様

### `/login`

`POST /login` では、DB 上の `users` テーブルに登録された user を使い、loginId / password による最小限の本人確認を行います。

現在の処理は次のとおりです。

- `LoginRequest` で `loginId` と `password` を受け取る
- `UserRepository#findByLoginId` で `users` テーブルから user を検索する
- user が存在しない場合は 401 を返す
- user が存在する場合、入力 password と `users.hashed_password` を `PasswordEncoder#matches` で照合する
- password が一致しない場合も 401 を返す
- user 未存在と password 不一致は、外部には同じメッセージとして返す
- 成功時は、JWT access token と `currentTenantId = null` を返す

### Request

```json
{
  "loginId": "test_taro",
  "password": "password_taro"
}
```

### Response

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "currentTenantId": null
}
```

`currentTenantId` は `/login` では確定させません。
current tenant の選択は、後続で実装する `POST /tenants/{tenantId}/select` の責務とします。

## JWT access token の扱い

`/login` 成功時に、JWT access token を発行します。

JWT には、現時点で以下の claim を含めています。

- `iss`: issuer
- `sub`: userId
- `iat`: 発行時刻
- `exp`: 有効期限

`sub` には、変更される可能性がある loginId ではなく、`users.id` を文字列化して入れています。

署名方式は HS256 です。

JWT の secret は Java コードに直接書かず、`jwt.secret` 経由で環境変数から読みます。

```yaml
jwt:
  issuer: orgflow-api
  secret: ${ORGFLOW_JWT_SECRET}
  access-token-expires-in: 1h
```

## Bearer token 検証

保護APIを呼ぶ場合、クライアントは以下のように JWT access token を送ります。

```http
Authorization: Bearer <accessToken>
```

Spring Security Resource Server が Controller 到達前に Bearer token を取り出し、`JwtDecoder` が JWT を検証します。

現時点では、以下を確認済みです。

| ケース                             | 結果 |
| ---------------------------------- | ---- |
| JWTなしで `GET /hello-auth`        | 401  |
| 正しいJWTで `GET /hello-auth`      | 200  |
| JWTを1文字削って `GET /hello-auth` | 401  |
| 期限切れJWTで `GET /hello-auth`    | 401  |

これにより、JWT access token を Bearer token として後続APIへ送信し、サーバー側で検証できる状態になっています。

ただし、現時点では issuer 検証は未実装です。
JWT に `iss` は含めていますが、`JwtDecoder` 側で `jwt.issuer` と一致するかを明示的に検証する処理は、次工程で扱います。

## 認証と認可の現在方針

Phase 1 では、Bearer token を使う認証済み API として設計します。

現時点の Spring Security 設定では、次の方針を採っています。

- `/login` は未認証で呼べる
- `/hello` は疎通確認用として未認証で呼べる
- `/error` はバリデーションエラーなどの処理が Security に遮られないように未認証で呼べる
- その他の API は、まず `authenticated` を基本とする
- CSRF は無効化する
- セッションは使わず stateless にする
- Spring Security のフォームログインは使わない
- Bearer token として送られた JWT は Spring Security Resource Server で検証する

request 系 / approval 系の細かい認可は、SecurityConfig に早く詰め込まず、Service / Policy 側で判定する方針です。

ここでいう細かい認可とは、たとえば次のような DB や業務状態を見ないと判断できない条件です。

- 対象 request が current tenant に属しているか
- user が対象 internal organization に所属しているか
- user が申請作成権限を持っているか
- user が現在の承認者か
- request が編集可能な状態か

## 現時点の主要エンドポイント

現時点で実装済みまたは確認用に使っている endpoint は次のとおりです。

| メソッド | path          | 目的                                           | 認証              |
| -------- | ------------- | ---------------------------------------------- | ----------------- |
| POST     | `/login`      | loginId / password 認証、JWT access token 発行 | 不要              |
| GET      | `/hello`      | 疎通確認                                       | 不要              |
| GET      | `/hello-auth` | JWT検証確認用の保護API                         | 必要              |
| -        | `/error`      | Spring Boot のエラー処理                       | Security 上は許可 |

`/hello-auth` は検証用APIです。
今後、実際の業務APIが保護APIとして実装されれば、削除または置き換える可能性があります。

## バリデーションエラーの扱い

`@Valid` による入力値検証でエラーが発生した場合、`GlobalExceptionHandler` で `MethodArgumentNotValidException` を受け、422 を返します。

これは、OpenAPI 上で入力値不正を 422 として扱う方針に合わせたものです。

現時点では、主に `/login` の `loginId` や `password` が空の場合に確認しています。

## 認証失敗時の扱い

`/login` の loginId / password 認証に失敗した場合は、401 を返します。

user が存在しない場合と password が一致しない場合は、外部には同じメッセージを返します。

これは、どちらが間違っているかを攻撃者に推測されにくくするためです。

一方で、JWTなし、改ざんJWT、期限切れJWTによる 401 は、Controller 到達前に Spring Security が返します。

そのため、`/login` の認証失敗による 401 と、保護APIのJWT検証失敗による 401 は、処理経路が異なります。

## ローカル起動

ローカル開発では、PostgreSQL を Docker Compose で起動します。

```bash
docker compose up -d db
```

Spring Boot API は、現時点ではローカルで起動し、Docker 上の PostgreSQL へ `localhost:5432` で接続します。

将来的に API も Docker Compose に含める場合、DB 接続先 host は `localhost` ではなく Compose の service 名である `db` に変更します。

## 環境変数

通常起動では、JWT署名用の secret を環境変数で設定します。

例:

```bash
export ORGFLOW_JWT_SECRET='your-256-bit-or-longer-secret-value'
```

その後、同じターミナルで Spring Boot を起動します。

```bash
cd api
./mvnw spring-boot:run
```

注意点として、shell の環境変数は設定したターミナルごとに有効です。
`export ORGFLOW_JWT_SECRET=...` を実行したターミナルと、Spring Boot を起動したターミナルが違う場合、Spring Boot 側ではその環境変数を読めません。

## テスト実行

テストは `api/` 配下で実行します。

```bash
cd api
./mvnw test
```

テスト実行時は `api/src/test/resources/application.yaml` の設定が使われます。
そのため、通常起動用の `api/src/main/resources/application.yaml` とは別に、テスト用のJWT設定が必要です。

現時点では、`contextLoads` による Spring Boot 起動確認が通る状態です。

## PostmanでのJWT確認手順

### 1. `/login` でJWTを取得する

```http
POST /login
Content-Type: application/json
```

```json
{
  "loginId": "test_taro",
  "password": "password_taro"
}
```

レスポンスの `accessToken` をコピーします。

### 2. `/hello-auth` をJWTなしで呼ぶ

```http
GET /hello-auth
```

期待結果:

```text
401 Unauthorized
```

### 3. `/hello-auth` を正しいJWT付きで呼ぶ

Postman の Authorization タブで、次のように設定します。

- Type: `Bearer Token`
- Token: `/login` で取得した `accessToken`

期待結果:

```text
200 OK
Hello auth!
```

### 4. JWTを改ざんして呼ぶ

Token の値を1文字削るなどしてから、再度 `GET /hello-auth` を呼びます。

期待結果:

```text
401 Unauthorized
```

### 5. 期限切れJWTを確認する

`access-token-expires-in` を一時的に短くして確認します。

例:

```yaml
jwt:
  access-token-expires-in: 10s
```

ログイン直後は `GET /hello-auth` が 200 になり、しばらく待ってから再実行すると 401 になることを確認します。

確認後は、必ず元の設定に戻します。

```yaml
jwt:
  access-token-expires-in: 1h
```

## リポジトリ構成

```text
.
├── README.md
├── api
│   ├── pom.xml
│   ├── mvnw
│   ├── mvnw.cmd
│   └── src
├── compose.yaml
├── docs
│   ├── adr
│   ├── design
│   ├── design-overview.md
│   ├── domain-rules.md
│   ├── er
│   └── openapi
└── notes
```

## 実装済み範囲と未実装範囲

### 実装済み

- Spring Boot API の最小構成
- PostgreSQL 接続
- Flyway migration
- seed user 登録
- `/login`
- loginId / password 認証
- BCrypt password 照合
- 認証失敗時の 401
- DTO バリデーションエラー時の 422
- JWT access token 発行
- Bearer token として送られた JWT の検証
- JWTなし / 正常JWT / 改ざんJWT / 期限切れJWT の確認
- `/hello-auth` による保護APIの確認

### 未実装

- issuer 検証
- JWT の `sub` から userId を取り出してアプリ側で利用する処理
- `POST /tenants/{tenantId}/select`
- current tenant 入り token の再発行
- tenant / role / membership を使った認可
- request / approval 系 API の実装
- login 成功 / 失敗の監査ログ記録
- JWT検証失敗時のエラーレスポンス形式の整理
- GitHub Actions CI の本格整備

## 次に実装する予定

次は、JWT検証フェーズの仕上げとして、issuer 検証を追加します。

現時点では、JWT に `iss` は含めていますが、`JwtDecoder` 側で `jwt.issuer` と一致するかを明示的に検証していません。

次工程では、以下を確認します。

1. `JwtDecoder` に issuer 検証を追加する
2. 正常 token で `/hello-auth` が 200 のままになることを確認する
3. issuer が想定外の token を拒否できるか確認する
4. 既存の JWTなし / 改ざんJWT / 期限切れJWT の確認が壊れていないことを確認する

その後、JWT の `sub` から userId を取り出し、アプリケーション側で「このリクエストは誰のものか」を扱う方法を整理します。

ただし、tenant / role による認可は、`sub` から userId を取り出す流れを確認した後に進めます。

## 補足

この README は、現時点のプロジェクト入口として最小限に保っています。

設計理由の詳細は ADR、業務ルールや構造の詳細は docs を参照してください。

現時点では、実装途中の内容も含まれます。
そのため、「実装済み」と「設計済み・未実装」は区別して記載します。
